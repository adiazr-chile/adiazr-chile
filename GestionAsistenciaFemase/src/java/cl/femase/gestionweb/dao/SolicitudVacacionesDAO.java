/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.business.VacacionesBp;
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.SolicitudVacacionesVO;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.UsuarioCentroCostoVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexander
 */
public class SolicitudVacacionesDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    
    /**
    * Retorna lista con solicitudes de vacaciones
    * 
    * @param _empresaId
    * @param _cencoId
    * @param _runEmpleado
    * @param _startDate
    * @param _endDate
     * @param _usuario
    * @param _propias
    * @param _estado
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
    public List<SolicitudVacacionesVO> getSolicitudes(String _empresaId,
            int _cencoId,
            String _runEmpleado,
            String _startDate,
            String _endDate,
            UsuarioVO _usuario,
            boolean _propias,
            String _estado,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<SolicitudVacacionesVO> lista = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        SolicitudVacacionesVO data;
        VacacionesBp vacacionesBp = new VacacionesBp(null);
        try{
            String sql = "SELECT "
                    + "sv.solic_id,"
                    + "to_char(sv.solic_fec_ingreso,'yyyy-MM-dd HH24:MI:SS') solic_fec_ingreso,"
                    + "sv.status_id,"
                    + "estado.status_label,"
                    + "sv.solic_inicio,"
                    + "sv.solic_fin,"
                    + "sv.empresa_id,"
                    + "sv.rut_empleado,"
                    + "e.empl_nombres || ' ' || e.empl_ape_paterno|| ' ' || e.empl_ape_materno nombre_empleado,"
                    + "sv.username_solicita,"
                    + "sv.username_aprueba_rechaza,"
                    + "sv.fechahora_aprueba_rechaza,"
                    + "sv.fechahora_cancela,"
                    + "coalesce(sv.nota_observacion,'') nota_observacion,"
                    + "vac.saldo_dias saldo_vacaciones,"
                    + "vac.dias_especiales,"
                    + "sv.dias_efectivos_solicitados "
                + "FROM solicitud_vacaciones sv "
                    + " inner join empleado e on (sv.empresa_id=e.empresa_id and sv.rut_empleado=e.empl_rut) "
                    + " inner join estado_solicitud estado on (sv.status_id = estado.status_id) "
                    + " left outer join admingestionweb.vacaciones vac "
                    + " on (sv.empresa_id = vac.empresa_id and sv.rut_empleado = vac.rut_empleado) "
                + " where 1 = 1 ";

            if (_propias){
                sql += " and (sv.username_solicita = '" + _usuario.getUsername() + "') ";
            }else{
                sql += " and (e.cenco_id = " + _cencoId + ") "
                    + " and (sv.username_solicita != '" + _usuario.getUsername() + "') "
                    + " and (sv.rut_empleado != '" + _runEmpleado + "') ";
            }
            if (_empresaId != null && _empresaId.compareTo("") != 0){        
                sql += " and (sv.empresa_id = '" + _empresaId + "') ";
            }
            if (_propias && _runEmpleado != null && _runEmpleado.compareTo("-1") != 0){        
                sql += " and (sv.rut_empleado = '" + _runEmpleado + "') ";
            }
            if (_endDate == null || _endDate.compareTo("") == 0){        
                _endDate = _startDate;
            }
            if (_startDate != null && _startDate.compareTo("") != 0){        
                sql += " and (sv.solic_inicio between '" + _startDate + "' and '" + _endDate + "') ";
            }
            if (_estado != null && _estado.compareTo("TODAS") != 0){
                sql += " and (sv.status_id = '" + _estado + "') ";
            }
            
            if (_usuario.getIdPerfil() == Constantes.ID_PERFIL_JEFE_TECNICO_NACIONAL){
                sql += " and (e.empl_id_cargo = " + Constantes.ID_CARGO_DIRECTOR + ") ";
            }
            
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println("[SolicitudVacacionesDAO."
                + "getSolicitudes]Sql: "+ sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[SolicitudVacacionesDAO.getSolicitudes]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new SolicitudVacacionesVO();
                data.setId(rs.getInt("solic_id"));
                data.setFechaIngreso(rs.getString("solic_fec_ingreso"));
                data.setEstadoId(rs.getString("status_id"));
                data.setEstadoLabel(rs.getString("status_label"));
                data.setInicioVacaciones(rs.getString("solic_inicio"));
                data.setFinVacaciones(rs.getString("solic_fin"));
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setNombreEmpleado(rs.getString("nombre_empleado"));
                data.setUsernameSolicita(rs.getString("username_solicita"));
                data.setUsernameApruebaRechaza(rs.getString("username_aprueba_rechaza"));
                data.setFechaHoraApruebaRechaza(rs.getString("fechahora_aprueba_rechaza"));
                data.setFechaHoraCancela(rs.getString("fechahora_cancela"));
                data.setNotaObservacion(rs.getString("nota_observacion"));
                data.setSaldoVacaciones(rs.getInt("saldo_vacaciones"));
                data.setDiasEspeciales(rs.getString("dias_especiales"));
                
//                int diasSolicitados = 
//                vacacionesBp.getDiasEfectivos(data.getInicioVacaciones(), 
//                    data.getFinVacaciones(), data.getDiasEspeciales());
                
                data.setDiasEfectivosVacacionesSolicitadas(rs.getInt("dias_efectivos_solicitados"));
                
                lista.add(data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[SolicitudVacacionesDAO.getSolicitudes]"
                + "Error: " + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[SolicitudVacacionesDAO.getSolicitudes]"
                    + "Error: " + ex.toString());
            }
        }
        
        return lista;
    }
    
    /**
    * Retorna lista con solicitudes de vacaciones pendientes para aprobar/rechazar
    * 
    * @param _empresaId
    * @param _runEmpleado
    * @param _startDate
    * @param _endDate
    * @param _usuario
    * @param _estado
    * @param _propias
    * @param _cencosUsuario
    * @param _jtStartIndex
    * @param _jtPageSize
    * @param _jtSorting
    * @return 
    */
    public List<SolicitudVacacionesVO> getSolicitudesAprobarRechazar(String _empresaId,
            String _runEmpleado,
            String _startDate,
            String _endDate,
            UsuarioVO _usuario,
            String _estado,
            boolean _propias,
            List<UsuarioCentroCostoVO> _cencosUsuario,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<SolicitudVacacionesVO> lista = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        SolicitudVacacionesVO data;
        VacacionesBp vacacionesBp = new VacacionesBp(null);
        try{
            String sql = "SELECT "
                + "sv.solic_id,"
                + "to_char(sv.solic_fec_ingreso,'yyyy-MM-dd HH24:MI:SS') solic_fec_ingreso,"
                + "sv.status_id,"
                + "estado.status_label,"
                + "sv.solic_inicio,"
                + "sv.solic_fin,"
                + "sv.empresa_id,"
                + "sv.rut_empleado,"
                + "e.empl_nombres || ' ' || e.empl_ape_paterno nombre_empleado,"
                + "sv.username_solicita,"
                + "sv.username_aprueba_rechaza,"
                + "sv.fechahora_aprueba_rechaza,"
                + "sv.fechahora_cancela,"
                + "coalesce(sv.nota_observacion,'') nota_observacion,"
                + "vac.saldo_dias saldo_vacaciones,"
                    + "vac.dias_especiales, "
                    + "sv.dias_efectivos_solicitados,cargo.cargo_nombre "
                + "FROM solicitud_vacaciones sv "
                    + " inner join empleado e on (sv.empresa_id=e.empresa_id and sv.rut_empleado=e.empl_rut) "
                    + " inner join estado_solicitud estado on (sv.status_id = estado.status_id) "
                    + " left outer join vacaciones vac on (sv.empresa_id = vac.empresa_id and sv.rut_empleado = vac.rut_empleado) "
                    + " left outer join cargo on (e.empl_id_cargo = cargo.cargo_id) "
                + " where 1 = 1 ";

            String strCencos = "";
            Iterator<UsuarioCentroCostoVO> it = _cencosUsuario.iterator();
            while(it.hasNext()){
                UsuarioCentroCostoVO cenco=it.next();
                strCencos += cenco.getCcostoId()+",";
            }
            if (!_cencosUsuario.isEmpty()){
                strCencos = strCencos.substring(0, strCencos.length() - 1);
            }
            
            sql += " and (e.cenco_id in (" + strCencos + ") "
                + " and (sv.username_solicita != '" + _usuario.getUsername() + "') "
                + " and (sv.rut_empleado != '" + _runEmpleado + "')) ";
            
            if (_empresaId != null && _empresaId.compareTo("") != 0){        
                sql += " and (sv.empresa_id = '" + _empresaId + "') ";
            }
            
            if (_propias && _runEmpleado != null && _runEmpleado.compareTo("-1") != 0){        
                sql += " and (sv.rut_empleado = '" + _runEmpleado + "') ";
            }
            
            if (_endDate == null || _endDate.compareTo("") == 0){        
                _endDate = _startDate;
            }
            
            if (_startDate != null && _startDate.compareTo("") != 0){        
                sql += " and (sv.solic_inicio between '" + _startDate + "' and '" + _endDate + "') ";
            }
            
            if (_estado != null && _estado.compareTo("TODAS") != 0){
                sql += " and (sv.status_id = '" + _estado + "') ";
            }
            
            if (_usuario.getIdPerfil() == Constantes.ID_PERFIL_JEFE_TECNICO_NACIONAL){
                sql += " and (e.empl_id_cargo = " + Constantes.ID_CARGO_DIRECTOR + ") ";
            }
            
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println("[SolicitudVacacionesDAO."
                + "getSolicitudesAprobarRechazar]Sql: "+ sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[SolicitudVacacionesDAO.getSolicitudesAprobarRechazar]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new SolicitudVacacionesVO();
                data.setId(rs.getInt("solic_id"));
                data.setFechaIngreso(rs.getString("solic_fec_ingreso"));
                data.setEstadoId(rs.getString("status_id"));
                data.setEstadoLabel(rs.getString("status_label"));
                data.setInicioVacaciones(rs.getString("solic_inicio"));
                data.setFinVacaciones(rs.getString("solic_fin"));
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setNombreEmpleado(rs.getString("nombre_empleado"));
                data.setUsernameSolicita(rs.getString("username_solicita"));
                data.setUsernameApruebaRechaza(rs.getString("username_aprueba_rechaza"));
                data.setFechaHoraApruebaRechaza(rs.getString("fechahora_aprueba_rechaza"));
                data.setFechaHoraCancela(rs.getString("fechahora_cancela"));
                data.setNotaObservacion(rs.getString("nota_observacion"));
                data.setSaldoVacaciones(rs.getInt("saldo_vacaciones"));
                data.setDiasEspeciales(rs.getString("dias_especiales"));
                
                data.setLabelEmpleado(data.getNombreEmpleado() + "(" + rs.getString("cargo_nombre") + ")");
                
//                int diasSolicitados = 
//                vacacionesBp.getDiasEfectivos(data.getInicioVacaciones(), 
//                    data.getFinVacaciones(), data.getDiasEspeciales());
                
                data.setDiasEfectivosVacacionesSolicitadas(rs.getInt("dias_efectivos_solicitados"));
                
                lista.add(data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[SolicitudVacacionesDAO.getSolicitudesAprobarRechazar]"
                + "Error: " + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[SolicitudVacacionesDAO.getSolicitudesAprobarRechazar]"
                    + "Error: " + ex.toString());
            }
        }
        
        return lista;
    }
    
    /**
    * Retorna datos de una solicitud
    * 
    * @param _id
    * @return 
    */
    public SolicitudVacacionesVO getSolicitudByKey(int _id){
        
        PreparedStatement ps        = null;
        ResultSet rs                = null;
        SolicitudVacacionesVO data  = null;
        VacacionesBp vacacionesBp   = new VacacionesBp(null);
        try{
            String sql = "SELECT "
                    + "sv.solic_id,"
                    + "to_char(sv.solic_fec_ingreso,'yyyy-MM-dd HH24:MI:SS') solic_fec_ingreso,"
                    + "sv.status_id,"
                    + "estado.status_label,"
                    + "sv.solic_inicio,"
                    + "sv.solic_fin,"
                    + "sv.empresa_id,"
                    + "sv.rut_empleado,"
                    + "e.empl_nombres || ' ' || e.empl_ape_paterno|| ' ' || e.empl_ape_materno nombre_empleado,"
                    + "sv.username_solicita,"
                    + "sv.username_aprueba_rechaza,"
                    + "sv.fechahora_aprueba_rechaza,"
                    + "sv.fechahora_cancela,"
                    + "coalesce(sv.nota_observacion,'') nota_observacion,"
                    + "vac.saldo_dias saldo_vacaciones,"
                    + "coalesce(vac.dias_especiales,'N') dias_especiales,"
                    + "sv.dias_efectivos_solicitados  "
                + "FROM solicitud_vacaciones sv "
                    + " inner join empleado e on (sv.empresa_id=e.empresa_id and sv.rut_empleado=e.empl_rut) "
                    + " inner join estado_solicitud estado on (sv.status_id = estado.status_id) "
                    + " left outer join admingestionweb.vacaciones vac "
                    + " on (sv.empresa_id = vac.empresa_id and sv.rut_empleado = vac.rut_empleado) "
                + " where sv.solic_id = " + _id;

            System.out.println("[SolicitudVacacionesDAO."
                + "getSolicitudes]Sql: "+ sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[SolicitudVacacionesDAO.getSolicitudByKey]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                data = new SolicitudVacacionesVO();
                data.setId(rs.getInt("solic_id"));
                data.setFechaIngreso(rs.getString("solic_fec_ingreso"));
                data.setEstadoId(rs.getString("status_id"));
                data.setEstadoLabel(rs.getString("status_label"));
                data.setInicioVacaciones(rs.getString("solic_inicio"));
                data.setFinVacaciones(rs.getString("solic_fin"));
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setRutEmpleado(rs.getString("rut_empleado"));
                data.setNombreEmpleado(rs.getString("nombre_empleado"));
                data.setUsernameSolicita(rs.getString("username_solicita"));
                data.setUsernameApruebaRechaza(rs.getString("username_aprueba_rechaza"));
                data.setFechaHoraApruebaRechaza(rs.getString("fechahora_aprueba_rechaza"));
                data.setFechaHoraCancela(rs.getString("fechahora_cancela"));
                data.setNotaObservacion(rs.getString("nota_observacion"));
                data.setSaldoVacaciones(rs.getInt("saldo_vacaciones"));
                data.setDiasEspeciales(rs.getString("dias_especiales"));
                
//                int diasSolicitados = 
//                vacacionesBp.getDiasEfectivos(data.getInicioVacaciones(), 
//                    data.getFinVacaciones(), data.getDiasEspeciales());
                
                data.setDiasEfectivosVacacionesSolicitadas(rs.getInt("dias_efectivos_solicitados"));
                
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[SolicitudVacacionesDAO.getSolicitudByKey]"
                + "Error: " + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[SolicitudVacacionesDAO.getSolicitudByKey]"
                    + "Error: " + ex.toString());
            }
        }
        
        return data;
    }
    
    /**
    * 
    * @param _empresaId
    * @param _rutEmpleado
    * @param _startDate
    * @param _endDate
    * @param _usuario
    * @param _propias
    * @param _estado
    * @return 
    */
    public int getSolicitudesCount(String _empresaId,
            String _rutEmpleado,
            String _startDate,
            String _endDate,
            UsuarioVO _usuario,
            boolean _propias,
            String _estado){
        int count=0;
        ResultSet rs;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[SolicitudVacacionesDAO.getSolicitudesCount]");
            try (Statement statement = dbConn.createStatement()) {
                String sql = "SELECT count(sv.solic_id) count "
                    + "FROM solicitud_vacaciones sv "
                    + " inner join empleado e "
                    + "  on (sv.empresa_id=e.empresa_id and sv.rut_empleado=e.empl_rut) "
                    + "where 1 = 1 ";
                
                if (_propias){
                    sql += " and sv.username_solicita = '" + _usuario.getUsername() + "' ";
                }
                
                if (_empresaId != null && _empresaId.compareTo("") != 0){        
                    sql += " and sv.empresa_id = '" + _empresaId + "'";
                }
                
                if (_rutEmpleado != null && _rutEmpleado.compareTo("") != 0){        
                    sql += " and sv.rut_empleado = '" + _rutEmpleado + "'";
                }
                
                if (_endDate == null || _endDate.compareTo("") == 0){        
                    _endDate = _startDate;
                }
                
                if (_startDate != null && _startDate.compareTo("") != 0){        
                    sql += " and sv.solic_inicio between '" + _startDate + "' and '" + _endDate + "' ";
                }
                
                if (_estado != null && _estado.compareTo("TODAS") != 0){
                    sql += " and (sv.status_id = '" + _estado + "') ";
                }
                
                if (_usuario.getIdPerfil() == Constantes.ID_PERFIL_JEFE_TECNICO_NACIONAL){
                    sql += " and (e.empl_id_cargo = " + Constantes.ID_CARGO_DIRECTOR + ") ";
                }
                
                rs = statement.executeQuery(sql);
                if (rs.next()) {
                    count=rs.getInt("count");
                }
            }
            rs.close();
            dbLocator.freeConnection(dbConn);
        } catch (SQLException|DatabaseException e) {
            e.printStackTrace();
        }finally{
            try {
                dbLocator.freeConnection(dbConn);
            } catch (Exception ex) {
                System.err.println("[SolicitudVacacionesDAO.getSolicitudesCount]"
                    + "Error: " + ex.toString());
            }
        }
        return count;
    }
    
    /**
    * 
    * @param _empresaId
    * @param _runEmpleado
    * @param _startDate
    * @param _endDate
    * @param _usuario
    * @param _estado
    * @param _propias
    * @param _cencosUsuario
    * @return 
    */
    public int getSolicitudesAprobarRechazarCount(String _empresaId,
            String _runEmpleado,
            String _startDate,
            String _endDate,
            UsuarioVO _usuario,
            String _estado,
            boolean _propias,
            List<UsuarioCentroCostoVO> _cencosUsuario){
        int count=0;
        ResultSet rs;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[SolicitudVacacionesDAO.getSolicitudesAprobarRechazarCount]");
            try (Statement statement = dbConn.createStatement()) {
                String sql = "SELECT count(sv.solic_id) count "
                    + "FROM solicitud_vacaciones sv "
                    + " inner join empleado e "
                    + "  on (sv.empresa_id = e.empresa_id "
                        + " and sv.rut_empleado = e.empl_rut) "
                    + "where 1 = 1 ";
                String strCencos = "";
                Iterator<UsuarioCentroCostoVO> it = _cencosUsuario.iterator();
                while(it.hasNext()){
                    UsuarioCentroCostoVO cenco=it.next();
                    strCencos += cenco.getCcostoId()+",";
                }
                if (!_cencosUsuario.isEmpty()){
                    strCencos = strCencos.substring(0, strCencos.length() - 1);
                }
                sql += " and (e.cenco_id in (" + strCencos + ") "
                    + " and (sv.username_solicita != '" + _usuario.getUsername() + "') "
                    + " and (sv.rut_empleado != '" + _runEmpleado + "')) ";
                
                if (_empresaId != null && _empresaId.compareTo("") != 0){        
                    sql += " and sv.empresa_id = '" + _empresaId + "'";
                }
                
                if (_propias && _runEmpleado != null && _runEmpleado.compareTo("") != 0){        
                    sql += " and sv.rut_empleado = '" + _runEmpleado + "'";
                }
                
                if (_endDate == null || _endDate.compareTo("") == 0){        
                    _endDate = _startDate;
                }
                
                if (_startDate != null && _startDate.compareTo("") != 0){        
                    sql += " and sv.solic_inicio between '" + _startDate + "' and '" + _endDate + "' ";
                }
                
                if (_estado != null && _estado.compareTo("TODAS") != 0){
                    sql += " and (sv.status_id = '" + _estado + "') ";
                }
                
                if (_usuario.getIdPerfil() == Constantes.ID_PERFIL_JEFE_TECNICO_NACIONAL){
                    sql += " and (e.empl_id_cargo = " + Constantes.ID_CARGO_DIRECTOR + ") ";
                }
                
                rs = statement.executeQuery(sql);
                if (rs.next()) {
                    count=rs.getInt("count");
                }
            }
            rs.close();
            dbLocator.freeConnection(dbConn);
        } catch (SQLException|DatabaseException e) {
            e.printStackTrace();
        }finally{
            try {
                dbLocator.freeConnection(dbConn);
            } catch (Exception ex) {
                System.err.println("[SolicitudVacacionesDAO.getSolicitudesAprobarRechazarCount]"
                    + "Error: " + ex.toString());
            }
        }
        return count;
    }
   
    /**
    * Agrega una nueva solicitud de vacaciones. Estado 'Pendiente'
    * @param _data
    * @return 
    */
    public MaintenanceVO insert(SolicitudVacacionesVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al insertar "
            + "Solicitud de vacaciones. "
            + " Empresa_id: " + _data.getEmpresaId()
            + ", rutEmpleado: " + _data.getRutEmpleado()
            + ", inicioVacaciones: " + _data.getInicioVacaciones()
            + ", finVacaciones: " + _data.getFinVacaciones()
            + ", dias efectivos solicitados: " + _data.getDiasEfectivosVacacionesSolicitadas()    
            + ", usernameSolicita: " + _data.getUsernameSolicita();
        
       String msgFinal = " Inserta Solicitud de vacaciones:"
            + "empresaId [" + _data.getEmpresaId() + "],"
            +  ", rutEmpleado [" + _data.getRutEmpleado() + "]"
            +  ", inicioVacaciones [" + _data.getInicioVacaciones() + "]"   
            +  ", finVacaciones [" + _data.getFinVacaciones() + "]"
            +  ", dias efectivos solicitados [" + _data.getDiasEfectivosVacacionesSolicitadas() + "]"   
            +  ", usernameSolicita [" + _data.getUsernameSolicita() + "]";            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO solicitud_vacaciones("
                + "solic_fec_ingreso,"
                + "status_id,"
                + "solic_inicio,"
                + "solic_fin,"
                + "empresa_id,"
                + "rut_empleado,"
                + "username_solicita, dias_efectivos_solicitados) "
                + " VALUES ('" + _data.getFechaIngreso() + "', "
                    + "?, "
                    + "'" + _data.getInicioVacaciones() + "', "
                    + "'" + _data.getFinVacaciones() + "', "
                    + "'" + _data.getEmpresaId() + "', "
                    + "'" + _data.getRutEmpleado() + "',"
                    + "'" + _data.getUsernameSolicita() + "', ?)";
                         
            dbConn = dbLocator.getConnection(m_dbpoolName,"[SolicitudVacacionesDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  Constantes.ESTADO_SOLICITUD_PENDIENTE);
            insert.setInt(2,  _data.getDiasEfectivosVacacionesSolicitadas());
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println("[SolicitudVacacionesDAO.insert]"
                    + " Empresa_id: " + _data.getEmpresaId()
                    + ", rutEmpleado: " + _data.getRutEmpleado()
                    + ", inicioVacaciones: " + _data.getInicioVacaciones()
                    + ", finVacaciones: " + _data.getFinVacaciones()
                    + ", dias efectivos solicitados: " + _data.getDiasEfectivosVacacionesSolicitadas()    
                    + ", usernameSolicita: " + _data.getUsernameSolicita()
                    + ", insert OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[SolicitudVacacionesDAO.insert]"
                + "insert solicitud_vacaciones Error_1: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError + " :" + sqle.toString());
        }finally{
            try {
                if (insert != null) insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[SolicitudVacacionesDAO.insert]"
                    + "insert comuna Error_2: " + ex.toString());
            }
        }
        return objresultado;
    }
    
    /**
    * Cancela una solicitud
    * @param _idSolicitud
    * @param _username
    * @param _fechaHoraCancelacion
    * @return 
    */
    public MaintenanceVO cancelarSolicitud(int _idSolicitud, 
            String _username, String _fechaHoraCancelacion){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al cancelar solicitud de vacaciones. "
            + "id: " + _idSolicitud
            + ", username: " + _username;
        try{
            String msgFinal = " Cancelar Solicitud de vacaciones:"
                + "id [" + _idSolicitud + "]" 
                + ", username [" + _username+ "]";
            System.out.println("[SolicitudVacacionesDAO.cancelarSolicitud]" + msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE solicitud_vacaciones "
                + " SET status_id = ?, "
                + "fechahora_cancela = '" + _fechaHoraCancelacion + "' "
                + " WHERE solic_id = ? "
                    + "and username_solicita = ? ";

            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[SolicitudVacacionesDAO.cancelarSolicitud]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  Constantes.ESTADO_SOLICITUD_CANCELADA);
            psupdate.setInt(2,  _idSolicitud);
            psupdate.setString(3,  _username);
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println("[SolicitudVacacionesDAO.cancelarSolicitud]"
                    + ", id: " + _idSolicitud
                    +" update OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[SolicitudVacacionesDAO.cancelarSolicitud]"
                + "Error_1: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError + " :" + sqle.toString());
        }finally{
            try {
                if (psupdate != null) psupdate.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[SolicitudVacacionesDAO.cancelarSolicitud]"
                    + "Error_2: " + ex.toString());
            }
        }

        return objresultado;
    }
    
    /**
    * Aprobar una solicitud
    * @param _idSolicitud
    * @param _fechaHoraAprobacion
    * @param _usernameAprueba
     * @param _notaObservacion
    * @return 
    */
    public MaintenanceVO aprobarSolicitud(int _idSolicitud, 
            String _usernameAprueba, 
            String _fechaHoraAprobacion,
            String _notaObservacion){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al aprobar solicitud de vacaciones. "
            + "id: " + _idSolicitud
            + ", usernameAprueba: " + _usernameAprueba
            + ", notaObservacion: " + _notaObservacion;
        
        try{
            String msgFinal = " Aprobar Solicitud de vacaciones:"
                + "id [" + _idSolicitud + "]" 
                + ", username aprueba [" + _usernameAprueba + "]"
                + ", notaObservacion [" + _notaObservacion + "]";
            System.out.println("[SolicitudVacacionesDAO.aprobarSolicitud]" + msgFinal);
            objresultado.setMsg(msgFinal);
            String sql = "UPDATE solicitud_vacaciones "
                + " SET status_id = ?, "
                    + "fechahora_aprueba_rechaza = '" + _fechaHoraAprobacion + "', "
                    + "username_aprueba_rechaza= '" + _usernameAprueba + "', "
                    + "nota_observacion= '" + _notaObservacion + "' "
                + " WHERE solic_id = ? ";

            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[SolicitudVacacionesDAO.aprobarSolicitud]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  Constantes.ESTADO_SOLICITUD_APROBADA);
            psupdate.setInt(2,  _idSolicitud);
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println("[SolicitudVacacionesDAO.aprobarSolicitud]"
                    + ", id: " + _idSolicitud
                    +" update OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[SolicitudVacacionesDAO.aprobarSolicitud]"
                + "Error_1: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError + " :" + sqle.toString());
        }finally{
            try {
                if (psupdate != null) psupdate.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[SolicitudVacacionesDAO.aprobarSolicitud]"
                    + "Error_2: " + ex.toString());
            }
        }

        return objresultado;
    }
    
    /**
    * Rechaza una solicitud
    * @param _idSolicitud
    * @param _fechaHoraRechazo
    * @param _usernameRechaza
    * @param _notaObservacion
    * @return 
    */
    public MaintenanceVO rechazarSolicitud(int _idSolicitud, 
            String _usernameRechaza, 
            String _fechaHoraRechazo, 
            String _notaObservacion){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al rechazar solicitud de vacaciones. "
            + "id: " + _idSolicitud
            + ", username rechaza: " + _usernameRechaza
            + ", nota_observacion: " + _notaObservacion;
        
        try{
            String msgFinal = " Rechazar Solicitud de vacaciones:"
                + "id [" + _idSolicitud + "]" 
                + ", username rechaza[" + _usernameRechaza + "]"
                    + ", nota_observacion[" + _notaObservacion + "]";
            
            System.out.println("[SolicitudVacacionesDAO.rechazarSolicitud]" + msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE solicitud_vacaciones "
                + " SET status_id = ?, "
                + " fechahora_aprueba_rechaza = '" + _fechaHoraRechazo + "', "
                + " username_aprueba_rechaza= '" + _usernameRechaza + "', "
                + " nota_observacion= '" + _notaObservacion + "' "    
                + " WHERE solic_id = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[SolicitudVacacionesDAO.rechazarSolicitud]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  Constantes.ESTADO_SOLICITUD_RECHAZADA);
            psupdate.setInt(2,  _idSolicitud);
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println("[SolicitudVacacionesDAO.rechazarSolicitud]"
                    + ", id: " + _idSolicitud
                    +" update OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[SolicitudVacacionesDAO.rechazarSolicitud]"
                + "Error_1: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError + " :" + sqle.toString());
        }finally{
            try {
                if (psupdate != null) psupdate.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[SolicitudVacacionesDAO.rechazarSolicitud]"
                    + "Error_2: " + ex.toString());
            }
        }

        return objresultado;
    }
    
}
