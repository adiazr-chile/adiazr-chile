/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.UsuarioCentroCostoVO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexander
 */
public class DashboardDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    
    /**
    * Retorna lista con empleados vigentes, sin ausencias y sin marcas a la fecha actual
    * 
    * @param _empresaId
    * @param _cencosUsuario
    * @return 
    */
    public ArrayList<EmpleadoVO> getEmpleadosSinMarcasTurnoNormal(String _empresaId,
            List<UsuarioCentroCostoVO> _cencosUsuario){
        ArrayList<EmpleadoVO> empleados = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        EmpleadoVO empleado;
        
        try{
            String ccostoIds = _cencosUsuario.stream()
                .map(UsuarioCentroCostoVO::getCcostoId)
                .map(String::valueOf) // por si ccostoId no es String
                .collect(Collectors.joining(","));
            
            String sql = "select " +
                    "e.empl_rut rut, " +
                    "e.empl_nombres nombres, "
                    + "e.empl_ape_paterno paterno, "
                    + "e.empl_ape_materno materno, " +
                    "e.empresa_id, " +
                    "e.cenco_id, " +
                    "e.empl_id_turno, " +
                    "t.nombre_turno, " +
                    "marca.rut_empleado, " +
                    "cc.ccosto_nombre cenco_nombre," + 
                    "cargo.cargo_nombre " +
                "from empleado e " +
                    " inner join turno t on (e.empl_id_turno = t.id_turno and t.empresa_id = '" + _empresaId + "') " +
                    " inner join centro_costo cc on (e.cenco_id = cc.ccosto_id) " + 
                    " inner join cargo on (e.empl_id_cargo = cargo.cargo_id) " +
                    "LEFT JOIN marca ON (e.empresa_id = marca.empresa_cod "
                        + "and e.empl_rut = marca.rut_empleado "
                        + "AND DATE(marca.fecha_hora) = CURRENT_DATE) " +
                "where e.empl_estado = 1 " +
                    "and marca.rut_empleado IS NULL " +
                    "and e.empl_fec_fin_contrato >= current_date " +
                    "and e.empresa_id = '" + _empresaId + "' ";
                
                if (!_cencosUsuario.isEmpty()){
                    sql += " and e.cenco_id in (" + ccostoIds + ") ";
                }    
                        
                sql += " and e.empl_id_turno not in ( " +
                    "    select t.id_turno " +
                    "    from turno t " +
                    "    where t.empresa_id = '" + _empresaId + "' " +
                    "    and t.rotativo " +
                    ") " +
                    "AND "
                        + "("
                        + " CASE "
                        + " WHEN EXTRACT(dow FROM CURRENT_DATE) = 0 THEN 7 "
                        + " ELSE EXTRACT(dow FROM CURRENT_DATE) "
                        + "END"
                        + ") IN "
                        + "("
                        + " SELECT cod_dia "
                        + " FROM detalle_turno dt "
                        + " WHERE dt.id_turno = e.empl_id_turno"
                        + ") " +     
                    "and e.empl_rut not in ( " +
                    "    select da.rut_empleado " +
                    "    from detalle_ausencia da " +
                    "    where current_date BETWEEN da.fecha_inicio AND da.fecha_fin " +
                    ") " +
                "order by e.empl_nombres, " +
                    "e.empl_ape_paterno, " +
                    "e.empl_ape_materno;";

            System.out.println("[DashboardDAO.getEmpleadosSinMarcasTurnoNormal]Sql: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[DashboardDAO.getEmpleadosSinMarcasTurnoNormal]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                empleado = new EmpleadoVO();
                empleado.setEmpresaId(rs.getString("empresa_id"));
                empleado.setRut(rs.getString("rut"));
                empleado.setNombres(rs.getString("nombres"));
                empleado.setApePaterno(rs.getString("paterno"));
                empleado.setApeMaterno(rs.getString("materno"));
                empleado.setNombreTurno(rs.getString("nombre_turno"));
                empleado.setCencoNombre(rs.getString("cenco_nombre"));
                empleado.setNombreCargo(rs.getString("cargo_nombre"));
                
                empleados.add(empleado);
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
                System.err.println("DashboardDAO.getEmpleadosSinMarcasTurnoNormal.Error: "+ex.toString());
            }
        }
        
        return empleados;
    }
    
    /**
    * Retorna lista con empleados con turno rotativo 
    * y que no tienen turno asignado para la fecha actual
    * 
    * @param _empresaId
    * @param _cencosUsuario
    * @return 
    */
    public ArrayList<EmpleadoVO> getEmpleadosConTurnoRotativoSinTurnoAsignado(String _empresaId,
            List<UsuarioCentroCostoVO> _cencosUsuario){
        ArrayList<EmpleadoVO> empleados = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        EmpleadoVO empleado;
        
        try{
            String ccostoIds = _cencosUsuario.stream()
                .map(UsuarioCentroCostoVO::getCcostoId)
                .map(String::valueOf) // por si ccostoId no es String
                .collect(Collectors.joining(","));
            
            String sql = "select "
                + "e.empl_rut rut, "
                + "e.empl_nombres nombres, "
                + "e.empl_ape_paterno paterno, "
                + "e.empl_ape_materno materno, "
                + "e.empresa_id, "
                + "e.cenco_id, "
                + "cc.ccosto_nombre cenco_nombre, "
                + "cargo.cargo_nombre "
            + "from empleado e "
                + "inner join centro_costo cc on (e.cenco_id = cc.ccosto_id) "
                + "inner join cargo on (e.empl_id_cargo = cargo.cargo_id) "
            + "where e.empl_id_turno in ("
                + "select t.id_turno "
                + "from turno t "
                + "where t.empresa_id = '" + _empresaId + "' and t.rotativo"
            + ") "
                + " and e.empresa_id = '" + _empresaId + "' "
                + " and e.empl_estado = 1 "
                + " and e.empl_fec_fin_contrato >= current_date "
                + " and (SELECT get_turno_rotativo_by_fecha_json(e.empresa_id, e.empl_rut, current_date::TEXT)) IS NULL ";
            if (!_cencosUsuario.isEmpty()){
                sql += " and e.cenco_id in (" + ccostoIds + ") ";
            }
            sql+= " order by e.empl_nombres, e.empl_ape_paterno, e.empl_ape_materno";
                
            System.out.println("[DashboardDAO."
                + "getEmpleadosConTurnoRotativoSinTurnoAsignado]Sql: " + sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,
                "[DashboardDAO.getEmpleadosConTurnoRotativoSinTurnoAsignado]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                empleado = new EmpleadoVO();
                empleado.setEmpresaId(rs.getString("empresa_id"));
                empleado.setRut(rs.getString("rut"));
                empleado.setNombres(rs.getString("nombres"));
                empleado.setApePaterno(rs.getString("paterno"));
                empleado.setApeMaterno(rs.getString("materno"));
                empleado.setCencoNombre(rs.getString("cenco_nombre"));
                empleado.setNombreCargo(rs.getString("cargo_nombre"));
                
                empleados.add(empleado);
                System.out.println("add empleado rut");
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("SQL Error: " + sqle.toString());
            m_logger.error("Error: " + sqle.toString());
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("DashboardDAO.getEmpleadosConTurnoRotativoSinTurnoAsignado.Error: "+ex.toString());
            }
        }
        
        return empleados;
    }
    
}
