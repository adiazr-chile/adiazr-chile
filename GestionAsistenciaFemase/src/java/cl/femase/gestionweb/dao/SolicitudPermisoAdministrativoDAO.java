/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.SolicitudPermisoAdministrativoVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
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
public class SolicitudPermisoAdministrativoDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    
    /**
    * Retorna lista con solicitudes de permiso administrativo
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
    public List<SolicitudPermisoAdministrativoVO> getSolicitudes(String _empresaId,
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
        
        List<SolicitudPermisoAdministrativoVO> lista = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        SolicitudPermisoAdministrativoVO data;
        
        try{
            String sql = "SELECT "
                    + "solicitud.solic_id,"
                    + "to_char(solicitud.solic_fec_ingreso,'yyyy-MM-dd HH24:MI:SS') solic_fec_ingreso,"
                    + "solicitud.status_id,"
                    + "estado.status_label,"
                    + "solicitud.solic_inicio,"
                    + "solicitud.solic_fin,"
                    + "solicitud.empresa_id,"
                    + "solicitud.run_empleado,"
                    + "e.empl_nombres || ' ' || e.empl_ape_paterno|| ' ' || e.empl_ape_materno nombre_empleado,"
                    + "solicitud.username_solicita,"
                    + "solicitud.username_aprueba_rechaza,"
                    + "solicitud.fechahora_aprueba_rechaza,"
                    + "solicitud.fechahora_cancela,"
                    + "coalesce(solicitud.nota_observacion,'') nota_observacion,"
                    + "solicitud.dias_solicitados,"
                    + "solicitud.anio,solicitud.semestre,"
                    + "solicitud.jornada,"
                    + "to_char(solicitud.hora_inicio, 'HH24:MI:SS') hora_inicio_str,"
                    + "to_char(solicitud.hora_fin, 'HH24:MI:SS') hora_fin_str "    
                + "FROM solicitud_permiso_administrativo solicitud "
                    + " inner join empleado e on (solicitud.empresa_id=e.empresa_id and solicitud.run_empleado=e.empl_rut) "
                    + "inner join estado_solicitud estado on (solicitud.status_id = estado.status_id)  "
                + " where 1 = 1";

            if (_propias){
                sql += " and (solicitud.username_solicita = '" + _usuario.getUsername() + "') ";
            }else{
                sql += " and (e.cenco_id = " + _cencoId + ") "
                    + " and (solicitud.username_solicita != '" + _usuario.getUsername() + "') "
                    + " and (solicitud.run_empleado != '" + _runEmpleado + "') ";
            }
            if (_empresaId != null && _empresaId.compareTo("") != 0){        
                sql += " and (solicitud.empresa_id = '" + _empresaId + "') ";
            }
            if (_propias && _runEmpleado != null && _runEmpleado.compareTo("-1") != 0){        
                sql += " and (solicitud.run_empleado = '" + _runEmpleado + "') ";
            }
            if (_endDate == null || _endDate.compareTo("") == 0){        
                _endDate = _startDate;
            }
            if (_startDate != null && _startDate.compareTo("") != 0){        
                sql += " and (solicitud.solic_inicio between '" + _startDate + "' and '" + _endDate + "') ";
            }
            if (_estado != null && _estado.compareTo("TODAS") != 0){
                sql += " and (solicitud.status_id = '" + _estado + "') ";
            }
            
            if (_usuario.getIdPerfil() == Constantes.ID_PERFIL_JEFE_TECNICO_NACIONAL){
                sql += " and (e.empl_id_cargo = " + Constantes.ID_CARGO_DIRECTOR + ") ";
            }
            
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println(WEB_NAME+"[SolicitudPermisoAdministrativoDAO."
                + "getSolicitudes]Sql: "+ sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[SolicitudPermisoAdministrativoDAO.getSolicitudes]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new SolicitudPermisoAdministrativoVO();
                data.setId(rs.getInt("solic_id"));
                data.setFechaIngreso(rs.getString("solic_fec_ingreso"));
                data.setEstadoId(rs.getString("status_id"));
                data.setEstadoLabel(rs.getString("status_label"));
                data.setFechaInicioPA(rs.getString("solic_inicio"));
                data.setFechaFinPA(rs.getString("solic_fin"));
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setRunEmpleado(rs.getString("run_empleado"));
                data.setNombreEmpleado(rs.getString("nombre_empleado"));
                data.setUsernameSolicita(rs.getString("username_solicita"));
                data.setUsernameApruebaRechaza(rs.getString("username_aprueba_rechaza"));
                data.setFechaHoraApruebaRechaza(rs.getString("fechahora_aprueba_rechaza"));
                data.setFechaHoraCancela(rs.getString("fechahora_cancela"));
                data.setNotaObservacion(rs.getString("nota_observacion"));
                
                data.setDiasSolicitados(rs.getDouble("dias_solicitados"));
                data.setAnio(rs.getInt("anio"));
                data.setSemestre(rs.getInt("semestre"));
                data.setJornada(rs.getString("jornada"));
                data.setHoraInicioPA_AMPM(rs.getString("hora_inicio_str"));
                data.setHoraFinPA_AMPM(rs.getString("hora_fin_str"));
                
                lista.add(data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[SolicitudPermisoAdministrativoDAO.getSolicitudes]"
                + "Error: " + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[SolicitudPermisoAdministrativoDAO.getSolicitudes]"
                    + "Error: " + ex.toString());
            }
        }
        
        return lista;
    }
    
    /**
    * Retorna lista con solicitudes de permiso administrativo pendientes para aprobar/rechazar
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
    public List<SolicitudPermisoAdministrativoVO> getSolicitudesAprobarRechazar(String _empresaId,
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
        
        List<SolicitudPermisoAdministrativoVO> lista = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        SolicitudPermisoAdministrativoVO data;
        
        try{
            String sql = "SELECT "
                    + "solicitud.solic_id,"
                    + "to_char(solicitud.solic_fec_ingreso,'yyyy-MM-dd HH24:MI:SS') solic_fec_ingreso,"
                    + "solicitud.status_id,"
                    + "estado.status_label,"
                    + "solicitud.solic_inicio,"
                    + "solicitud.solic_fin,"
                    + "solicitud.empresa_id,"
                    + "solicitud.run_empleado,"
                    + "e.empl_nombres || ' ' || e.empl_ape_paterno nombre_empleado,"
                    + "solicitud.username_solicita,"
                    + "solicitud.username_aprueba_rechaza,"
                    + "solicitud.fechahora_aprueba_rechaza,"
                    + "solicitud.fechahora_cancela,"
                    + "coalesce(solicitud.nota_observacion,'') nota_observacion,"
                    + "cargo.cargo_nombre,"
                    + "solicitud.dias_solicitados,"
                    + "solicitud.anio,solicitud.semestre,"
                    + "solicitud.jornada,"
                    + "to_char(solicitud.hora_inicio, 'HH24:MI:SS') hora_inicio_str,"
                    + "to_char(solicitud.hora_fin, 'HH24:MI:SS') hora_fin_str "
                + "FROM solicitud_permiso_administrativo solicitud "
                    + " inner join empleado e on (solicitud.empresa_id=e.empresa_id and solicitud.run_empleado=e.empl_rut) "
                    + " inner join estado_solicitud estado on (solicitud.status_id = estado.status_id) "
                    //+ " left outer join vacaciones vac on (sv.empresa_id = vac.empresa_id and sv.rut_empleado = vac.rut_empleado) "
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
                + " and (solicitud.username_solicita != '" + _usuario.getUsername() + "') "
                + " and (solicitud.run_empleado != '" + _runEmpleado + "')) ";
            
            if (_empresaId != null && _empresaId.compareTo("") != 0){        
                sql += " and (solicitud.empresa_id = '" + _empresaId + "') ";
            }
            
            if (_propias && _runEmpleado != null && _runEmpleado.compareTo("-1") != 0){        
                sql += " and (solicitud.run_empleado = '" + _runEmpleado + "') ";
            }
            
            if (_endDate == null || _endDate.compareTo("") == 0){        
                _endDate = _startDate;
            }
            
            if (_startDate != null && _startDate.compareTo("") != 0){        
                sql += " and (solicitud.solic_inicio between '" + _startDate + "' and '" + _endDate + "') ";
            }
            
            if (_estado != null && _estado.compareTo("TODAS") != 0){
                sql += " and (solicitud.status_id = '" + _estado + "') ";
            }
            
            if (_usuario.getIdPerfil() == Constantes.ID_PERFIL_JEFE_TECNICO_NACIONAL){
                sql += " and (e.empl_id_cargo = " + Constantes.ID_CARGO_DIRECTOR + ") ";
            }
            
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            
            System.out.println(WEB_NAME+"[SolicitudPermisoAdministrativoDAO."
                + "getSolicitudesAprobarRechazar]Sql: "+ sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[SolicitudPermisoAdministrativoDAO.getSolicitudesAprobarRechazar]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new SolicitudPermisoAdministrativoVO();
                data.setId(rs.getInt("solic_id"));
                data.setFechaIngreso(rs.getString("solic_fec_ingreso"));
                data.setEstadoId(rs.getString("status_id"));
                data.setEstadoLabel(rs.getString("status_label"));
                data.setFechaInicioPA(rs.getString("solic_inicio"));
                data.setFechaFinPA(rs.getString("solic_fin"));
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setRunEmpleado(rs.getString("run_empleado"));
                data.setNombreEmpleado(rs.getString("nombre_empleado"));
                data.setUsernameSolicita(rs.getString("username_solicita"));
                data.setUsernameApruebaRechaza(rs.getString("username_aprueba_rechaza"));
                data.setFechaHoraApruebaRechaza(rs.getString("fechahora_aprueba_rechaza"));
                data.setFechaHoraCancela(rs.getString("fechahora_cancela"));
                data.setNotaObservacion(rs.getString("nota_observacion"));
                data.setDiasSolicitados(rs.getDouble("dias_solicitados"));
                data.setAnio(rs.getInt("anio"));
                data.setSemestre(rs.getInt("semestre"));
                data.setJornada(rs.getString("jornada"));
                
                data.setLabelEmpleado(data.getNombreEmpleado() + "(" + rs.getString("cargo_nombre") + ")");
                
                data.setHoraInicioPA_AMPM(rs.getString("hora_inicio_str"));
                data.setHoraFinPA_AMPM(rs.getString("hora_fin_str"));
                
                lista.add(data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[SolicitudPermisoAdministrativoDAO.getSolicitudesAprobarRechazar]"
                + "Error: " + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[SolicitudPermisoAdministrativoDAO.getSolicitudesAprobarRechazar]"
                    + "Error: " + ex.toString());
            }
        }
        
        return lista;
    }
    
    /**
    * Retorna datos de una solicitud de permiso administrativo
    * 
    * @param _id
    * @return 
    */
    public SolicitudPermisoAdministrativoVO getSolicitudByKey(int _id){
        
        PreparedStatement ps        = null;
        ResultSet rs                = null;
        SolicitudPermisoAdministrativoVO data  = null;
       
        try{
            String sql = "SELECT "
                    + "solicitud.solic_id,"
                    + "to_char(solicitud.solic_fec_ingreso,'yyyy-MM-dd HH24:MI:SS') solic_fec_ingreso,"
                    + "solicitud.status_id,"
                    + "estado.status_label,"
                    + "solicitud.solic_inicio,"
                    + "solicitud.solic_fin,"
                    + "solicitud.empresa_id,"
                    + "solicitud.run_empleado,"
                    + "e.empl_nombres || ' ' || e.empl_ape_paterno|| ' ' || e.empl_ape_materno nombre_empleado,"
                    + "solicitud.username_solicita,"
                    + "solicitud.username_aprueba_rechaza,"
                    + "solicitud.fechahora_aprueba_rechaza,"
                    + "solicitud.fechahora_cancela,"
                    + "coalesce(solicitud.nota_observacion,'') nota_observacion,"
                    + "solicitud.dias_solicitados,"
                    + "solicitud.anio,solicitud.semestre,"
                    + "solicitud.jornada,"
                    + "to_char(solicitud.hora_inicio, 'HH24:MI:SS') hora_inicio_str,"
                    + "to_char(solicitud.hora_fin, 'HH24:MI:SS') hora_fin_str "
                + "FROM solicitud_permiso_administrativo solicitud "
                + " inner join empleado e on (solicitud.empresa_id=e.empresa_id and solicitud.run_empleado=e.empl_rut) "
                + "inner join estado_solicitud estado on (solicitud.status_id = estado.status_id)  "
                + " where solicitud.solic_id = " + _id;

            System.out.println(WEB_NAME+"[SolicitudPermisoAdministrativoDAO."
                + "getSolicitudes]Sql: "+ sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[SolicitudPermisoAdministrativoDAO.getSolicitudByKey]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                data = new SolicitudPermisoAdministrativoVO();
                data.setId(rs.getInt("solic_id"));
                data.setFechaIngreso(rs.getString("solic_fec_ingreso"));
                data.setEstadoId(rs.getString("status_id"));
                data.setEstadoLabel(rs.getString("status_label"));
                data.setFechaInicioPA(rs.getString("solic_inicio"));
                data.setFechaFinPA(rs.getString("solic_fin"));
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setRunEmpleado(rs.getString("run_empleado"));
                data.setNombreEmpleado(rs.getString("nombre_empleado"));
                data.setUsernameSolicita(rs.getString("username_solicita"));
                data.setUsernameApruebaRechaza(rs.getString("username_aprueba_rechaza"));
                data.setFechaHoraApruebaRechaza(rs.getString("fechahora_aprueba_rechaza"));
                data.setFechaHoraCancela(rs.getString("fechahora_cancela"));
                data.setNotaObservacion(rs.getString("nota_observacion"));
               
                data.setDiasSolicitados(rs.getDouble("dias_solicitados"));
                data.setAnio(rs.getInt("anio"));
                data.setSemestre(rs.getInt("semestre"));
                data.setJornada(rs.getString("jornada"));
                
                data.setHoraInicioPA_AMPM(rs.getString("hora_inicio_str"));
                data.setHoraFinPA_AMPM(rs.getString("hora_fin_str"));
                
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[SolicitudPermisoAdministrativoDAO.getSolicitudByKey]"
                + "Error: " + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[SolicitudPermisoAdministrativoDAO.getSolicitudByKey]"
                    + "Error: " + ex.toString());
            }
        }
        
        return data;
    }
    
    /**
    * 
    * @param _empresaId
    * @param _runEmpleado
    * @param _startDate
    * @param _endDate
    * @param _usuario
    * @param _propias
    * @param _estado
    * @return 
    */
    public int getSolicitudesCount(String _empresaId,
            String _runEmpleado,
            String _startDate,
            String _endDate,
            UsuarioVO _usuario,
            boolean _propias,
            String _estado){
        int count=0;
        ResultSet rs;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[SolicitudPermisoAdministrativoDAO.getSolicitudesCount]");
            try (Statement statement = dbConn.createStatement()) {
                String sql = "SELECT count(spa.solic_id) count "
                    + "FROM solicitud_permiso_administrativo spa "
                    + " inner join empleado e "
                    + "  on (spa.empresa_id = e.empresa_id and spa.run_empleado = e.empl_rut) "
                    + "where 1 = 1 ";
                
                if (_propias){
                    sql += " and spa.username_solicita = '" + _usuario.getUsername() + "' ";
                }
                
                if (_empresaId != null && _empresaId.compareTo("") != 0){        
                    sql += " and spa.empresa_id = '" + _empresaId + "'";
                }
                
                if (_runEmpleado != null && _runEmpleado.compareTo("") != 0){        
                    sql += " and spa.run_empleado = '" + _runEmpleado + "'";
                }
                
                if (_endDate == null || _endDate.compareTo("") == 0){        
                    _endDate = _startDate;
                }
                
                if (_startDate != null && _startDate.compareTo("") != 0){        
                    sql += " and spa.solic_inicio between '" + _startDate + "' and '" + _endDate + "' ";
                }
                
                if (_estado != null && _estado.compareTo("TODAS") != 0){
                    sql += " and (spa.status_id = '" + _estado + "') ";
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
                System.err.println("[SolicitudPermisoAdministrativoDAO.getSolicitudesCount]"
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
            dbConn = dbLocator.getConnection(m_dbpoolName,"[SolicitudPermisoAdministrativoDAO.getSolicitudesAprobarRechazarCount]");
            try (Statement statement = dbConn.createStatement()) {
                String sql = "SELECT count(spa.solic_id) count "
                    + "FROM solicitud_permiso_administrativo spa "
                    + " inner join empleado e "
                    + "  on (spa.empresa_id = e.empresa_id "
                        + " and spa.run_empleado = e.empl_rut) "
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
                    + " and (spa.username_solicita != '" + _usuario.getUsername() + "') "
                    + " and (spa.run_empleado != '" + _runEmpleado + "')) ";
                
                if (_empresaId != null && _empresaId.compareTo("") != 0){        
                    sql += " and spa.empresa_id = '" + _empresaId + "'";
                }
                
                if (_propias && _runEmpleado != null && _runEmpleado.compareTo("") != 0){        
                    sql += " and spa.run_empleado = '" + _runEmpleado + "'";
                }
                
                if (_endDate == null || _endDate.compareTo("") == 0){        
                    _endDate = _startDate;
                }
                
                if (_startDate != null && _startDate.compareTo("") != 0){        
                    sql += " and spa.solic_inicio between '" + _startDate + "' and '" + _endDate + "' ";
                }
                
                if (_estado != null && _estado.compareTo("TODAS") != 0){
                    sql += " and (spa.status_id = '" + _estado + "') ";
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
                System.err.println("[SolicitudPermisoAdministrativoDAO.getSolicitudesAprobarRechazarCount]"
                    + "Error: " + ex.toString());
            }
        }
        return count;
    }
   
    /**
    * Agrega una nueva solicitud de Permiso administrativo. Estado 'Pendiente'
    * @param _data
    * @return 
    */
    public ResultCRUDVO insert(SolicitudPermisoAdministrativoVO _data){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        int result=0;
        String msgError = "Error al insertar "
            + "Solicitud de Permiso Administrativo (PA). "
            + " Empresa_id: " + _data.getEmpresaId()
            + ", runEmpleado: " + _data.getRunEmpleado()
            + ", anio: " + _data.getAnio()
            + ", semestre: " + _data.getSemestre()    
            + ", inicio PA: " + _data.getFechaInicioPA()
            + ", fin PA: " + _data.getFechaFinPA()
            + ", dias solicitados: " + _data.getDiasSolicitados()
            + ", jornada: " + _data.getJornada()  
            + ", hora inicio: " + _data.getHoraInicioPA_AMPM()
            + ", hora fin: " + _data.getHoraFinPA_AMPM()    
            + ", usernameSolicita: " + _data.getUsernameSolicita();
        
       String msgFinal = " Inserta Solicitud de Permiso Administrativo (PA):"
            + "empresaId [" + _data.getEmpresaId() + "],"
            +  ", runEmpleado [" + _data.getRunEmpleado() + "]"
            +  ", anio [" + _data.getAnio() + "]"
            +  ", semestre [" + _data.getSemestre() + "]"   
            +  ", inicio PA [" + _data.getFechaInicioPA() + "]"      
            +  ", fin PA [" + _data.getFechaFinPA() + "]"
            +  ", dias solicitados [" + _data.getDiasSolicitados() + "]"   
            +  ", jornada [" + _data.getJornada() + "]"
            +  ", hora inicio [" + _data.getHoraInicioPA_AMPM() + "]"
            +  ", hora fin [" + _data.getHoraFinPA_AMPM() + "]"   
            +  ", usernameSolicita [" + _data.getUsernameSolicita() + "]";            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String hraInicio    = null;
            String hraFin       = null;   
            if (_data.getHoraInicioPA_AMPM() != null){
                hraInicio = "'" + _data.getHoraInicioPA_AMPM() + "'";
            }
            if (_data.getHoraFinPA_AMPM() != null){
                hraFin = "'" + _data.getHoraFinPA_AMPM() + "'";
            }
            
            String strSql = "INSERT INTO solicitud_permiso_administrativo ("
                    + "solic_fec_ingreso,"
                    + "status_id,"
                    + "solic_inicio,"
                    + "solic_fin,"
                    + "empresa_id,"
                    + "run_empleado,"
                    + "username_solicita, "
                    + "dias_solicitados, "
                    + "anio, "
                    + "jornada, semestre";
                if (_data.getJornada().compareTo(Constantes.JORNADA_PERMISO_ADMINISTRATIVO_TODO_EL_DIA) != 0){    
                    strSql += ",hora_inicio,hora_fin";
                }
                strSql += ") ";
                
                strSql += " VALUES ('" + _data.getFechaIngreso() + "', "
                    + "?, "
                    + "'" + _data.getFechaInicioPA() + "', "
                    + "'" + _data.getFechaFinPA() + "', "
                    + "'" + _data.getEmpresaId() + "', "
                    + "'" + _data.getRunEmpleado() + "',"
                    + "'" + _data.getUsernameSolicita() + "', ?, ?, ?, ? ";
                if (_data.getJornada().compareTo(Constantes.JORNADA_PERMISO_ADMINISTRATIVO_TODO_EL_DIA) != 0){
                    strSql += ", '" + _data.getHoraInicioPA_AMPM() + "',";
                    strSql += "'" + _data.getHoraFinPA_AMPM() + "'";
                }
                strSql += ")";
            System.out.println(WEB_NAME+"[SolicitudPermisoAdministrativoDAO."
                + "insert]Sql: " + strSql);             
            dbConn = dbLocator.getConnection(m_dbpoolName,"[SolicitudPermisoAdministrativoDAO.insert]");
            insert = dbConn.prepareStatement(strSql);
            insert.setString(1,  Constantes.ESTADO_SOLICITUD_PENDIENTE);
            insert.setDouble(2,  _data.getDiasSolicitados());
            insert.setInt(3,  _data.getAnio());
            insert.setString(4,  _data.getJornada());
            insert.setInt(5,  _data.getSemestre());
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println(WEB_NAME+"[SolicitudPermisoAdministrativoDAO.insert]"
                    + " Empresa_id: " + _data.getEmpresaId()
                    + ", runEmpleado: " + _data.getRunEmpleado()
                    + ", anio: " + _data.getAnio()    
                    + ", inicio PA: " + _data.getFechaInicioPA()
                    + ", fin PA: " + _data.getFechaFinPA()
                    + ", dias solicitados: " + _data.getDiasSolicitados() 
                    + ", jornada: " + _data.getJornada()    
                    + ", hora inicio: " + _data.getHoraInicioPA_AMPM()
                    + ", hora fin: " + _data.getHoraFinPA_AMPM()        
                    + ", usernameSolicita: " + _data.getUsernameSolicita()
                    + ", insert OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[SolicitudPermisoAdministrativoDAO.insert]"
                + "insert solicitud_permiso_administrativo Error_1: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError + " :" + sqle.toString());
        }finally{
            try {
                if (insert != null) insert.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[SolicitudPermisoAdministrativoDAO.insert]"
                    + "insert Error_2: " + ex.toString());
            }
        }
        return objresultado;
    }
    
    /**
    * Cancela una solicitud de permiso administrativo
    * 
    * @param _idSolicitud
    * @param _username
    * @param _fechaHoraCancelacion
    * @return 
    */
    public ResultCRUDVO cancelarSolicitud(int _idSolicitud, 
            String _username, String _fechaHoraCancelacion){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al cancelar solicitud de permiso administrativo. "
            + "id: " + _idSolicitud
            + ", username: " + _username;
        try{
            String msgFinal = " Cancelar Solicitud de permiso administrativo:"
                + "id [" + _idSolicitud + "]" 
                + ", username [" + _username+ "]";
            System.out.println(WEB_NAME+"[SolicitudPermisoAdministrativoDAO.cancelarSolicitud]" + msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE solicitud_permiso_administrativo "
                + " SET status_id = ?, "
                + "fechahora_cancela = '" + _fechaHoraCancelacion + "' "
                + " WHERE solic_id = ? "
                    + "and username_solicita = ? ";

            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[SolicitudPermisoAdministrativoDAO.cancelarSolicitud]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  Constantes.ESTADO_SOLICITUD_CANCELADA);
            psupdate.setInt(2,  _idSolicitud);
            psupdate.setString(3,  _username);
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println(WEB_NAME+"[SolicitudPermisoAdministrativoDAO.cancelarSolicitud]"
                    + ", id: " + _idSolicitud
                    +" update OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[SolicitudPermisoAdministrativoDAO.cancelarSolicitud]"
                + "Error_1: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError + " :" + sqle.toString());
        }finally{
            try {
                if (psupdate != null) psupdate.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[SolicitudPermisoAdministrativoDAO.cancelarSolicitud]"
                    + "Error_2: " + ex.toString());
            }
        }

        return objresultado;
    }
    
    /**
    * Aprobar una solicitud de permiso administrativo
    * 
    * @param _idSolicitud
    * @param _fechaHoraAprobacion
    * @param _usernameAprueba
     * @param _notaObservacion
    * @return 
    */
    public ResultCRUDVO aprobarSolicitud(int _idSolicitud, 
            String _usernameAprueba, 
            String _fechaHoraAprobacion,
            String _notaObservacion){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al aprobar solicitud de permiso administrativo. "
            + "id: " + _idSolicitud
            + ", usernameAprueba: " + _usernameAprueba
            + ", notaObservacion: " + _notaObservacion;
        
        try{
            String msgFinal = " Aprobar Solicitud de permiso administrativo:"
                + "id [" + _idSolicitud + "]" 
                + ", username aprueba [" + _usernameAprueba + "]"
                + ", notaObservacion [" + _notaObservacion + "]";
            System.out.println(WEB_NAME+"[SolicitudPermisoAdministrativoDAO.aprobarSolicitud]" + msgFinal);
            objresultado.setMsg(msgFinal);
            String sql = "UPDATE solicitud_permiso_administrativo "
                + " SET status_id = ?, "
                    + "fechahora_aprueba_rechaza = '" + _fechaHoraAprobacion + "', "
                    + "username_aprueba_rechaza= '" + _usernameAprueba + "', "
                    + "nota_observacion= '" + _notaObservacion + "' "
                + " WHERE solic_id = ? ";

            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[SolicitudPermisoAdministrativoDAO.aprobarSolicitud]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  Constantes.ESTADO_SOLICITUD_APROBADA);
            psupdate.setInt(2,  _idSolicitud);
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println(WEB_NAME+"[SolicitudPermisoAdministrativoDAO.aprobarSolicitud]"
                    + ", id: " + _idSolicitud
                    +" update OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[SolicitudPermisoAdministrativoDAO.aprobarSolicitud]"
                + "Error_1: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError + " :" + sqle.toString());
        }finally{
            try {
                if (psupdate != null) psupdate.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[SolicitudPermisoAdministrativoDAO.aprobarSolicitud]"
                    + "Error_2: " + ex.toString());
            }
        }

        return objresultado;
    }
    
    /**
    * Rechaza una solicitud de permiso administrativo
    * 
    * @param _idSolicitud
    * @param _fechaHoraRechazo
    * @param _usernameRechaza
    * @param _notaObservacion
    * @return 
    */
    public ResultCRUDVO rechazarSolicitud(int _idSolicitud, 
            String _usernameRechaza, 
            String _fechaHoraRechazo, 
            String _notaObservacion){
        ResultCRUDVO objresultado = new ResultCRUDVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al rechazar solicitud de permiso administrativo. "
            + "id: " + _idSolicitud
            + ", username rechaza: " + _usernameRechaza
            + ", nota_observacion: " + _notaObservacion;
        
        try{
            String msgFinal = " Rechazar Solicitud de permiso administrativo:"
                + "id [" + _idSolicitud + "]" 
                + ", username rechaza[" + _usernameRechaza + "]"
                    + ", nota_observacion[" + _notaObservacion + "]";
            
            System.out.println(WEB_NAME+"[SolicitudPermisoAdministrativoDAO.rechazarSolicitud]" + msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE solicitud_permiso_administrativo "
                + " SET status_id = ?, "
                + " fechahora_aprueba_rechaza = '" + _fechaHoraRechazo + "', "
                + " username_aprueba_rechaza= '" + _usernameRechaza + "', "
                + " nota_observacion= '" + _notaObservacion + "' "    
                + " WHERE solic_id = ?";

            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[SolicitudPermisoAdministrativoDAO.rechazarSolicitud]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  Constantes.ESTADO_SOLICITUD_RECHAZADA);
            psupdate.setInt(2,  _idSolicitud);
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println(WEB_NAME+"[SolicitudPermisoAdministrativoDAO.rechazarSolicitud]"
                    + ", id: " + _idSolicitud
                    +" update OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[SolicitudPermisoAdministrativoDAO.rechazarSolicitud]"
                + "Error_1: " + sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError + " :" + sqle.toString());
        }finally{
            try {
                if (psupdate != null) psupdate.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("[SolicitudPermisoAdministrativoDAO.rechazarSolicitud]"
                    + "Error_2: " + ex.toString());
            }
        }

        return objresultado;
    }
    
}
