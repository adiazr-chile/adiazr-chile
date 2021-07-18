/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.vo.GraficoAusenciaVO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class GraficosDAO extends BaseDAO{

    //private Connection dbConn;
    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();
    
    public GraficosDAO() {
        //dbConn = dbLocator.getConnection(m_dbpoolName);
    }
    
    /**
     * Retorna total de marcas de entrada/salida 
     * para la fecha, el rango horario y el tipo de marca especificados
     * 
     * @param _fecha
     * @param _startTime
     * @param _endTime
     * @param _tipoMarca
     * 
     * @return 
     */
    public int getTotalMarcas(String _fecha, 
            String _startTime, 
            String _endTime, 
            int _tipoMarca){ 
        
        int totalMarcas = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            dbConn = dbLocator.getConnection(m_dbpoolName,"[GraficosDAO.getTotalMarcas]");
            String sql = "select count(fecha_hora) marcas "
                + "from marca "
                + "where fecha_hora::date = '"+_fecha+"' "
                    + "and fecha_hora::time between '"+_startTime+"' and '"+_endTime+"' and cod_tipo_marca= " + _tipoMarca
                + " group by fecha_hora::date";
            //System.out.println("[GraficosDAO.getTotalMarcas]sql: " + sql);
            
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                totalMarcas = rs.getInt("marcas");
            }

            ps.close();
            rs.close();
            dbConn.close();
            dbLocator.freeConnection(dbConn);
        }catch(Exception  sqle){
            System.err.println("[Treeview.GraficosDAO."
                + "getTotalMarcas]Error: "+sqle.toString());
            sqle.printStackTrace();
        }finally{
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                //dbLocator.freeConnection(dbConn);
            } catch (SQLException ex) {
                System.err.println("Error: "+ex.toString());
            }
        }
        return totalMarcas;
    }
    
    /**
     * Retorna maxima fecha con marcas.
     * 
     * @return 
     */
    public String getMaximaFechaConMarcas(){ 
        
        String fecha = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            dbConn = dbLocator.getConnection(m_dbpoolName,"[GraficosDAO.getMaximaFechaConMarcas]");
            String sql = "select"
                + " to_char(max(fecha_hora),'yyyy-MM-dd') maxima "
                + "from marca";
            
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                fecha = rs.getString("maxima");
            }

            ps.close();
            rs.close();
            dbConn.close();
            dbLocator.freeConnection(dbConn);
        }catch(Exception  sqle){
            System.err.println("[Treeview.GraficosDAO."
                + "getMaximaFechaConMarcas]Error: "+sqle.toString());
            sqle.printStackTrace();
        }
        return fecha;
    }
    
    /**
     * Retorna total horas de atraso para una fecha especifica
     * 
     * @param _fecha
       * 
     * @return 
     */
    public double getTotalHrsAtraso(String _fecha){ 
        
        double totalHrsAtraso = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            dbConn = dbLocator.getConnection(m_dbpoolName,"[GraficosDAO.getTotalHrsAtraso]");
            String sql = "select "
                + " ( ((sum(DATE_PART('hour', m.fecha_hora::time - dt.hora_entrada::time)) * 60) + "
                    + "sum(DATE_PART('minute', m.fecha_hora::time - dt.hora_entrada::time))+ "
                    + "(sum(DATE_PART('second', m.fecha_hora::time - dt.hora_entrada::time))/60))/60 ) total_horas_atraso "
                + " from marca m "
                + " inner join empleado e on (m.rut_empleado = e.empl_rut) "
                + " inner join detalle_turno dt on (dt.id_turno = e.empl_id_turno and dt.cod_dia = (select extract(dow from m.fecha_hora::date))) "
                + " and fecha_hora::date='" + _fecha + "' and m.cod_tipo_marca = 1 "
                + " and DATE_PART('minute', m.fecha_hora::time - dt.hora_entrada::time) > 0 "
                + "group by m.fecha_hora::date";
            System.out.println("getAtrasoFecha: " + sql);
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                totalHrsAtraso = rs.getDouble("total_horas_atraso");
            }

            ps.close();
            rs.close();
            dbConn.close();
            dbLocator.freeConnection(dbConn);
        }catch(Exception  sqle){
            System.err.println("[Treeview.GraficosDAO."
                + "getTotalHrsAtraso]Error: "+sqle.toString());
            sqle.printStackTrace();
        }
        return totalHrsAtraso;
    }
    
    /**
     * Retorna total horas de atraso para una fecha especifica
     * 
     * @param _anioMes
     * @return 
     */
    public double getTotalHrsAtrasoMes(String _anioMes){ 
        
        double totalHrsAtraso = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            dbConn = dbLocator.getConnection(m_dbpoolName,"[GraficosDAO.getTotalHrsAtrasoMes]");
            String sql = "select "
                + "( ((sum(DATE_PART('hour', m.fecha_hora::time - dt.hora_entrada::time)) * 60) + sum(DATE_PART('minute', m.fecha_hora::time - dt.hora_entrada::time))+ (sum(DATE_PART('second', m.fecha_hora::time - dt.hora_entrada::time))/60))/60 ) total_horas_atraso "
                + "from marca m "
                + "inner join empleado e on (m.rut_empleado = e.empl_rut) "
                + "inner join detalle_turno dt on (dt.id_turno = e.empl_id_turno and dt.cod_dia = "
                + "(select extract(dow from m.fecha_hora::date))) "
                + " and DATE_PART('minute', m.fecha_hora::time - dt.hora_entrada::time) > 0 and m.cod_tipo_marca=1 "
                + " and to_char(fecha_hora::date,'yyyy-MM') = '" + _anioMes + "' "
                + "group by m.fecha_hora::date";
            System.out.println("[getTotalHrsAtrasoMes]sql: "+sql);
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                totalHrsAtraso += rs.getDouble("total_horas_atraso");
            }

            ps.close();
            rs.close();
            dbConn.close();
            dbLocator.freeConnection(dbConn);
        }catch(Exception  sqle){
            System.err.println("[Treeview.GraficosDAO."
                + "getTotalHrsAtraso]Error: "+sqle.toString());
            sqle.printStackTrace();
        }
        return totalHrsAtraso;
    }
    
    /**
     * Retorna total horas de ausencias dentro de un mes
     * 
     * @param _anioMes
     * @return 
     */
    public List<GraficoAusenciaVO> getAusencias(String _anioMes){ 
        List<GraficoAusenciaVO> lista=new ArrayList<>();
        GraficoAusenciaVO detalle;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            dbConn = dbLocator.getConnection(m_dbpoolName,"[GraficosDAO.getAusencias]");
            String sql = "select ausencia.ausencia_id, "
                    + "ausencia.ausencia_nombre, "
                    + "count(detalle.rut_empleado) justificaciones "
                    + "from detalle_ausencia detalle "
                    + "inner join ausencia on (ausencia.ausencia_id=detalle.ausencia_id) "
                    + "where to_char(detalle.fecha_inicio,'yyyy-MM') = '"+_anioMes+"' "
                    + "group by ausencia.ausencia_id "
                    + "order by justificaciones";
            System.out.println("[getAusencias]sql: "+sql);
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                detalle = new GraficoAusenciaVO(rs.getString("ausencia_nombre"),rs.getInt("justificaciones"));
                lista.add(detalle);
            }

            ps.close();
            rs.close();
            dbConn.close();
            dbLocator.freeConnection(dbConn);
        }catch(Exception  sqle){
            System.err.println("[Treeview.GraficosDAO."
                + "getAusencias]Error: "+sqle.toString());
            sqle.printStackTrace();
        }
        return lista;
    }
    
    public void closeConnection(){
        try {
            dbLocator.freeConnection(dbConn);
        } catch (Exception ex) {
            System.out.println("[Treeview.GraficosDAO."
                + "closeConnection]Error al cerrar conecion"+ex.toString());
        }
    }
}
