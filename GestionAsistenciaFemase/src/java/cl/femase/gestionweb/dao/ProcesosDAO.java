/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.vo.MaintenanceVO;
import cl.femase.gestionweb.vo.ProcesoEjecucionVO;
import cl.femase.gestionweb.vo.ProcesoFiltroVO;
import cl.femase.gestionweb.vo.ProcesoProgramacionVO;
import cl.femase.gestionweb.vo.ProcesoVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexander
 */
public class ProcesosDAO extends BaseDAO{

    protected static Logger m_logger = Logger.getLogger("gestionweb");
    //private Connection dbConn;
    //private DatabaseLocator dbLoc;
    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();
    
    public ProcesosDAO(PropertiesVO _propsValues) {

    }
   
    
    /**
     * Retorna lista con los procesos existentes en el sistema
     * 
     * @param _empresaId
     * @param _nombre
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
     */
    public List<ProcesoVO> getProcesos(String _empresaId, String _nombre,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<ProcesoVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        ProcesoVO data;
        
        try{
            String sql ="SELECT "
                + "empresa_id, proc_id, "
                + "proc_name,proc_estado,"
                + "to_char(proc_fec_actualizacion, 'yyyy-MM-dd HH24:MI:SS') fecha_actualizacion,"
                + "proc_jobname "
                + " FROM proceso "
                + "where empresa_id='" + _empresaId + "' ";
           
            if (_nombre!=null && _nombre.compareTo("")!=0){        
                sql += " and upper(proc_name) like '%"+_nombre.toUpperCase()+"%'";
            }
           
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            System.out.println("[ProcesosDAO.getProcesos]-2-Sql: "+sql);
            dbConn = dbLocator.getConnection(m_dbpoolName,"[ProcesosDAO.getProcesos]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new ProcesoVO();
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setId(rs.getInt("proc_id"));
                data.setNombre(rs.getString("proc_name"));
                data.setEstado(rs.getInt("proc_estado"));
                data.setFechaHoraActualizacion(rs.getString("fecha_actualizacion"));
                data.setJobName(rs.getString("proc_jobname"));
                data.setRowKey(data.getEmpresaId() + "|" + data.getId());
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
    * Retorna lista con los procesos existentes en el sistema
    * 
    * @param _empresaId
    * @param _idProceso
    * @return 
    */
    public ProcesoVO getProceso(String _empresaId, int _idProceso){
        PreparedStatement ps = null;
        ResultSet rs    = null;
        ProcesoVO data  = null;
        
        try{
            String sql ="SELECT "
                + "empresa_id, proc_id, "
                + "proc_name,proc_estado,"
                + "to_char(proc_fec_actualizacion, 'yyyy-MM-dd HH24:MI:SS') fecha_actualizacion,"
                + "proc_jobname "
                + " FROM proceso "
                + "where empresa_id = '" + _empresaId + "' "
                    + "and proc_id = " +_idProceso;
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[ProcesosDAO.getProceso]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                data = new ProcesoVO();
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setId(rs.getInt("proc_id"));
                data.setNombre(rs.getString("proc_name"));
                data.setEstado(rs.getInt("proc_estado"));
                data.setFechaHoraActualizacion(rs.getString("fecha_actualizacion"));
                data.setJobName(rs.getString("proc_jobname"));
                data.setRowKey(data.getEmpresaId() + "|" + data.getId());
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("Error: "+sqle.toString());
        }
        return data;
    }
    
    /**
    * Retorna lista con los procesos existentes en el sistema
    * 
    * @param _empresaId
    * @param _idProceso
    * @return 
    */
    public ProcesoVO getProcesoByJob(String _empresaId, int _idProceso){
        PreparedStatement ps = null;
        ResultSet rs    = null;
        ProcesoVO data  = null;
        
        try{
            String sql ="SELECT "
                + "empresa_id, proc_id, "
                + "proc_name,proc_estado,"
                + "to_char(proc_fec_actualizacion, 'yyyy-MM-dd HH24:MI:SS') fecha_actualizacion,"
                + "proc_jobname "
                + " FROM proceso "
                + "where empresa_id = '" + _empresaId + "' "
                    + "and proc_id = " +_idProceso;
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[ProcesosDAO.getProceso]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                data = new ProcesoVO();
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setId(rs.getInt("proc_id"));
                data.setNombre(rs.getString("proc_name"));
                data.setEstado(rs.getInt("proc_estado"));
                data.setFechaHoraActualizacion(rs.getString("fecha_actualizacion"));
                data.setJobName(rs.getString("proc_jobname"));
                data.setRowKey(data.getEmpresaId() + "|" + data.getId());
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("Error: "+sqle.toString());
        }
        return data;
    }
    
    /**
     * Retorna lista con los procesos existentes en el sistema.
     * 
     * @param _empresaId
     * @param _nombre
     * @return 
     */
    public List<ProcesoVO> getProcesos(String _empresaId, 
            String _nombre){
        
        List<ProcesoVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        ProcesoVO data;
        
        try{
            String sql ="SELECT "
                    + "empresa_id, proc_id, "
                    + "proc_name,proc_estado,"
                    + "to_char(proc_fec_actualizacion, 'yyyy-MM-dd HH24:MI:SS') fecha_actualizacion,"
                    + "proc_jobname "
                + "FROM proceso "
                + "WHERE empresa_id='" + _empresaId + "' and proc_estado = 1 ";
           
            if (_nombre!=null && _nombre.compareTo("") != 0){        
                sql += " and upper(proc_name) like '%"+_nombre.toUpperCase()+"%'";
            }
            
            sql += " order by empresa_id, proc_id";
            System.out.println("[ProcesosDAO.getProcesos]-1-Sql: "+sql);
            dbConn = dbLocator.getConnection(m_dbpoolName,"[ProcesosDAO.getProcesos]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new ProcesoVO();
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setId(rs.getInt("proc_id"));
                data.setNombre(rs.getString("proc_name"));
                data.setEstado(rs.getInt("proc_estado"));
                data.setFechaHoraActualizacion(rs.getString("fecha_actualizacion"));
                data.setJobName(rs.getString("proc_jobname"));
                
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
    * Retorna lista con los procesos existentes en el sistema.
    * 
    * @param _empresaId
    * @param _jobName
    * @return 
    */
    public ProcesoVO getProcesoByJobName(String _empresaId, 
            String _jobName){
        
        ProcesoVO proceso = null;
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            String sql ="SELECT "
                    + "empresa_id, proc_id, "
                    + "proc_name,proc_estado,"
                    + "to_char(proc_fec_actualizacion, 'yyyy-MM-dd HH24:MI:SS') fecha_actualizacion,"
                    + "proc_jobname "
                + "FROM proceso "
                + "WHERE empresa_id='" + _empresaId + "' and proc_estado = 1 ";
           
            if (_jobName != null && _jobName.compareTo("") != 0){        
                sql += " and proc_jobname = ' " + _jobName + "'";
            }
            
            sql += " order by empresa_id, proc_id";
            System.out.println("[ProcesosDAO.getProcesoByJobName]-1-Sql: " + sql);
            dbConn = dbLocator.getConnection(m_dbpoolName,"[ProcesosDAO.getProcesoByJobName]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                proceso = new ProcesoVO();
                proceso.setEmpresaId(rs.getString("empresa_id"));
                proceso.setId(rs.getInt("proc_id"));
                proceso.setNombre(rs.getString("proc_name"));
                proceso.setEstado(rs.getInt("proc_estado"));
                proceso.setFechaHoraActualizacion(rs.getString("fecha_actualizacion"));
                proceso.setJobName(rs.getString("proc_jobname"));
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("[ProcesosDAO.getProcesoByJobName]Error: "+sqle.toString());
        }
        return proceso;
    }
    
    
    /**
     * Retorna lista con los procesos que deben 
     * ser ejecutados el dia especificado.
     * 
     * @param _dia
     * @return 
     */
    public List<ProcesoProgramacionVO> getProgramacionProcesosDia(int _dia){
        
        List<ProcesoProgramacionVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        ProcesoProgramacionVO data;
        
        try{
            String sql = "select "
                + "proc.proc_id,"
                + "proc.empresa_id,"
                + "proc.proc_jobname,"
                + "prog.horas_ejecucion "
                + "from proceso_programacion prog "
                + "inner join proceso proc "
                + "on (prog.empresa_id = proc.empresa_id "
                + "and prog.proc_id = proc.proc_id) "
                + "where prog.cod_dia = " + _dia
                + " order by proc.proc_id";
            System.out.println("[ProcesosDAO.getProgramacionProcesosDia]Sql: "+sql);    
            dbConn = dbLocator.getConnection(m_dbpoolName,"[ProcesosDAO.getProgramacionProcesosDia]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new ProcesoProgramacionVO();
                data.setProcesoId(rs.getInt("proc_id"));
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setJobName(rs.getString("proc_jobname"));
                data.setHorasEjecucion(rs.getString("horas_ejecucion"));
                
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
     * @param _nombre
     * @return 
     */
    public int getProcesosCount(String _empresaId, String _nombre){
        int count=0;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[ProcesosDAO.getProcesosCount]");
            Statement statement = dbConn.createStatement();
            String strSql ="SELECT count(proc_id) "
                + "FROM proceso "
                + "where empresa_id='" + _empresaId + "' ";
               
            if (_nombre!=null && _nombre.compareTo("")!=0){        
                strSql += " and upper(proc_name) like '%"+_nombre.toUpperCase()+"%'";
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
   
    /**
     * Actualiza una empresa
     * @param _data
     * @return 
     */
    public MaintenanceVO update(ProcesoVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        PreparedStatement psupdate = null;
        int result=0;
        String msgError = "Error al actualizar "
            + "proceso. "
            + "empresaId: "+_data.getEmpresaId()
            + ", idProceso: "+_data.getId()
            + ", nombre: "+_data.getNombre()
            + ", jobName: "+_data.getJobName()
            + ", estado: "+_data.getEstado();
        
        try{
            String msgFinal = " Actualiza proceso:"
                + " EmpresaId [" + _data.getEmpresaId() + "]" 
                + ", idProceso [" + _data.getId() + "]" 
                + ", nombre [" + _data.getNombre() + "]"
                + ", jobName [" + _data.getJobName()+ "]"    
                + ", estado [" + _data.getEstado() + "]";
            
            System.out.println(msgFinal);
            objresultado.setMsg(msgFinal);
            
            String sql = "UPDATE proceso "
                + "SET proc_name=?, "
                + "proc_estado=?, "
                + "proc_fec_actualizacion= current_timestamp,"
                + "proc_jobname=? "
                + "WHERE empresa_id = ? "
                    + "and proc_id = ?";
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[ProcesosDAO.update]");
            psupdate = dbConn.prepareStatement(sql);
            psupdate.setString(1,  _data.getNombre());
            psupdate.setInt(2,  _data.getEstado());
            psupdate.setString(3,  _data.getJobName());
            psupdate.setString(4,  _data.getEmpresaId());
            psupdate.setInt(5,  _data.getId());
            
            int rowAffected = psupdate.executeUpdate();
            if (rowAffected == 1){
                System.out.println("[update]proceso."
                    + " empresaId:" +_data.getEmpresaId()
                    + ", idProceso:" +_data.getId()
                    + ", nombre:" +_data.getNombre()    
                    + ", jobName:" +_data.getJobName()
                    + ", estado:" +_data.getEstado()    
                    +" actualizado OK!");
            }

            psupdate.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("update proceso Error: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }

        return objresultado;
    }

    /**
     * Agrega un nuevo proceso
     * @param _data
     * @return 
     */
    public MaintenanceVO insert(ProcesoVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al insertar "
            + "proceso. "
            + " empresaId: " + _data.getEmpresaId()    
            + ", nombre: " + _data.getNombre()
            + ", jobName: " + _data.getJobName()
            + ", estado: " + _data.getEstado();
        
       String msgFinal = " Inserta proceso:"
            + "EmpresaId [" + _data.getEmpresaId() + "]"
            + " nombre [" + _data.getNombre() + "]"   
            + " jobName [" + _data.getJobName() + "]"
            + " estado [" + _data.getEstado() + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO proceso("
                + "empresa_id, proc_id, proc_name, "
                + "proc_estado, proc_fec_actualizacion, proc_jobname) "
                + "VALUES (?, nextval('proceso_id_seq'), ?, ?, current_timestamp, ?)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[ProcesosDAO.insert]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1, _data.getEmpresaId());
            insert.setString(2, _data.getNombre());
            insert.setInt(3, _data.getEstado());
            insert.setString(4, _data.getJobName());
            
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println("[insert proceso]"
                    + " empresaId:" +_data.getEmpresaId()
                    + ", nombre:" +_data.getNombre()
                    + ", jobName:" +_data.getJobName()
                    + ", estado:" +_data.getEstado()
                    +" insertado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("insert proceso Error1: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }

        return objresultado;
    }
    
    /**
     * ***************************************************
     * ********* Metodos para programacion_proceso *******
     * ***************************************************
     */
    
    /**
     * Retorna lista con la programacion de un proceso especifico.
     * 
     * @param _empresaId
     * @param _idProceso
     * @param _codDia
     * @return 
     */
    public ProcesoProgramacionVO getProgramacion(String _empresaId, int _idProceso, int _codDia){
        PreparedStatement ps = null;
        ResultSet rs = null;
        ProcesoProgramacionVO data=null;
        
        try{
            String sql ="SELECT "
                + "empresa_id, proc_id, "
                + "cod_dia, horas_ejecucion "
                + "FROM proceso_programacion "
                + "WHERE empresa_id='" + _empresaId + "' "
                + "and proc_id = " + _idProceso 
                + " and cod_dia = " + _codDia;
            
            System.out.println("[GestionFemase."
                + "ProcesosDAO.getProgramacion]Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[ProcesosDAO.getProgramacion]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()){
                data = new ProcesoProgramacionVO();
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setProcesoId(rs.getInt("proc_id"));
                data.setCodDia(rs.getInt("cod_dia"));
                data.setHorasEjecucion(rs.getString("horas_ejecucion"));
              
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("Error: "+sqle.toString());
        }
        return data;
    }
    
    /**
     * Retorna lista con los filtros de un proceso especifico.
     *  Estos filtros permiten ejecutar dicho proceso.
     * 
     * @param _empresaId
     * @param _idProceso
     * @return 
     */
    public LinkedHashMap<String, ProcesoFiltroVO> getFiltros(String _empresaId, int _idProceso){
        PreparedStatement ps = null;
        ResultSet rs = null;
        LinkedHashMap<String, ProcesoFiltroVO> filtros
            =new LinkedHashMap<>();
        ProcesoFiltroVO data=null;
        Locale localeCl = new Locale("es", "CL");
        Calendar mycal  = Calendar.getInstance(localeCl);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Date dteEndDate = Utilidades.restaDias(6);
        String startDate    = sdf.format(dteEndDate);
        String endDate      = sdf.format(mycal.getTime());
        try{
            String sql ="SELECT "
                    + "proceso.empresa_id,"
                    + "proceso.proc_id,"
                    + "proceso.proc_name,"
                    + "proceso_filtro.filtro_code,"
                    + "filtro.filtro_label,"
                    + "proceso_filtro.orden,"
                    + "filtro.filtro_list,"
                    + "filtro.filtro_list_source_table,"
                    + "filtro.filtro_isdate,"
                    + "filtro.filtro_checkbox,"
                    + "filtro.default_value," 
                    +  "filtro.format "
                + "FROM "
                    + "filtro,"
                    + "proceso_filtro, "
                    + "proceso "
                + "WHERE "
                    + "proceso_filtro.filtro_code = filtro.filtro_code AND "
                    + "proceso.empresa_id = proceso_filtro.empresa_id AND "
                    + "proceso.proc_id = proceso_filtro.proc_id AND "
                    + "proceso.empresa_id = '" + _empresaId + "' AND "
                    + "proceso.proc_id = " + _idProceso
                + " order by proceso_filtro.orden";
            
            System.out.println("[GestionFemase."
                + "ProcesosDAO.getFiltros]Sql: "+sql);
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[ProcesosDAO.getFiltros]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new ProcesoFiltroVO();
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setProcesoId(rs.getInt("proc_id"));
                data.setCode(rs.getString("filtro_code"));
                data.setLabel(rs.getString("filtro_label"));
                data.setIsList(rs.getBoolean("filtro_list"));
                data.setSourceTable(rs.getString("filtro_list_source_table"));
                data.setIsDate(rs.getBoolean("filtro_isdate"));
                data.setIsCheckbox(rs.getBoolean("filtro_checkbox"));
                data.setFormat(rs.getString("format"));
                
                if (data.getCode().compareTo("start_date")==0){
                    data.setDefaultValue(startDate);
                }else if (data.getCode().compareTo("end_date")==0){
                    data.setDefaultValue(endDate);
                }else if (data.getCode().compareTo("date")==0){
                    data.setDefaultValue(sdf.format(mycal.getTime()));
                }
                
                filtros.put(data.getCode(), data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("Error: "+sqle.toString());
        }
        return filtros;
    }
    
    /**
     * Agrega un nuevo resgistro de programacion para el proceso
     * 
     * @param _data
     * @return 
     */
    public MaintenanceVO insertProgramacion(ProcesoProgramacionVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al insertar "
            + "programacion proceso. "
            + " empresaId: " + _data.getEmpresaId()    
            + ", idProceso: " + _data.getProcesoId()
            + ", codDia: " + _data.getCodDia()
            + ", hrasEjecucion: " + _data.getHorasEjecucion();
        
       String msgFinal = " Inserta programacion proceso:"
            + "EmpresaId [" + _data.getEmpresaId() + "]"
            + " idProceso [" + _data.getProcesoId() + "]"   
            + " codDia [" + _data.getCodDia() + "]"
            + " hrasEjecucion [" + _data.getHorasEjecucion() + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO proceso_programacion"
                + " (empresa_id, proc_id, "
                + "cod_dia, horas_ejecucion) "
                + " VALUES (?, ?, ?, ?)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[ProcesosDAO.insertProgramacion]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1, _data.getEmpresaId());
            insert.setInt(2, _data.getProcesoId());
            insert.setInt(3, _data.getCodDia());
            insert.setString(4, _data.getHorasEjecucion());
                        
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println("[insert proceso_programacion]"
                    + " empresaId:" + _data.getEmpresaId()
                    + ", idProceso:" + _data.getProcesoId()
                    + ", codDia:" + _data.getCodDia()
                    + ", hrasEjecucion:" + _data.getHorasEjecucion()
                    +" insertado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("insert proceso_programacion Error1: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }

        return objresultado;
    }
    
    /**
     * Agrega un nuevo registro de itinerario de ejecucion para un proceso
     * 
     * @param _data
     * @return 
     */
    public MaintenanceVO insertItinerario(ProcesoEjecucionVO _data){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al insertar "
            + "itinerario ejecucion proceso. "
            + " empresaId: " + _data.getEmpresaId()
            + ", deptoId: " + _data.getDeptoId()
            + ", cencoId: " + _data.getCencoId()    
            + ", idProceso: " + _data.getProcesoId()
            + ", inicio: " + _data.getFechaHoraInicioEjecucion()
            + ", fin: " + _data.getFechaHoraFinEjecucion()
            + ", usuario: " + _data.getUsuario();
        
       String msgFinal = " Inserta itinerario ejecucion proceso:"
            + "EmpresaId [" + _data.getEmpresaId() + "]"
            + " DeptoId [" + _data.getDeptoId() + "]"
            + " CencoId [" + _data.getCencoId() + "]"   
            + " idProceso [" + _data.getProcesoId() + "]"   
            + " fechaHoraInicio [" + _data.getFechaHoraInicioEjecucion() + "]"
            + " fechaHoraFin [" + _data.getFechaHoraFinEjecucion() + "]"
            + " usuario [" + _data.getUsuario() + "]";
            
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "INSERT INTO proceso_itinerario_ejecucion("
                + "empresa_id, "
                + "proc_id, "
                + "fecha_inicio_ejecucion, "
                + "fecha_fin_ejecucion, "
                + "resultado_ejecucion,"
                + "exec_user, "
                + "depto_id, "
                + "cenco_id) "
                + "VALUES (?, ?, "
                    + "'"+_data.getFechaHoraInicioEjecucion()+"', "
                    + "'"+_data.getFechaHoraFinEjecucion()+"', ?, ?, ?, ?)";

            dbConn = dbLocator.getConnection(m_dbpoolName,"[ProcesosDAO.insertItinerario]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1, _data.getEmpresaId());
            insert.setInt(2, _data.getProcesoId());
            insert.setString(3, _data.getResultado());
            insert.setString(4, _data.getUsuario());
            insert.setString(5, _data.getDeptoId());
            insert.setInt(6, _data.getCencoId());
                        
            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1){
                System.out.println("[insert proceso_itinerario_ejecucion]"
                    + " empresaId:" + _data.getEmpresaId()
                    + ", idProceso:" + _data.getProcesoId()
                    + ", inicio:" + _data.getFechaHoraInicioEjecucion()
                    + ", fin:" + _data.getFechaHoraFinEjecucion()
                    +" insertado OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("insert proceso_itinerario_ejecucion Error1: "+sqle.toString());
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
     * Elimina la programacion del proceso para un dia x
     * 
     * @param _programacion
     * @return 
     */
    public MaintenanceVO deleteProgramacion(ProcesoProgramacionVO _programacion){
        MaintenanceVO objresultado = new MaintenanceVO();
        int result=0;
        String msgError = "Error al eliminar "
            + "programacion_proceso, "
            + "empresaId: " + _programacion.getEmpresaId()
            + ", idProceso: " + _programacion.getProcesoId()
            + ", codDia: " + _programacion.getCodDia();
        
       String msgFinal = " Elimina programacion_proceso:"
            + "empresaId [" + _programacion.getEmpresaId() + "]" 
            + ", idProceso [" +  _programacion.getProcesoId() + "]"
            + ", codDia [" + _programacion.getCodDia() + "]";
       
        objresultado.setMsg(msgFinal);
        PreparedStatement insert    = null;
        
        try{
            String sql = "DELETE FROM proceso_programacion "
                + "WHERE empresa_id = ? "
                + "and proc_id = ? ";
            
            if (_programacion.getCodDia() != -1){
                sql+= "and cod_dia = ?";
            }
            dbConn = dbLocator.getConnection(m_dbpoolName,"[ProcesosDAO.deleteProgramacion]");
            insert = dbConn.prepareStatement(sql);
            insert.setString(1,  _programacion.getEmpresaId());
            insert.setInt(2,  _programacion.getProcesoId());
            if (_programacion.getCodDia() != -1){
                insert.setInt(3,  _programacion.getCodDia());
            }
            int filasAfectadas = insert.executeUpdate();
            m_logger.debug("[delete proceso_programacion]filasAfectadas: "+filasAfectadas);
            if (filasAfectadas == 1){
                m_logger.debug("[delete proceso_programacion]"
                    + ", empresaId:" + _programacion.getEmpresaId()
                    + ", idProceso:" + _programacion.getProcesoId()
                    + ", codDia:" + _programacion.getCodDia()
                    +" eliminada OK!");
            }
            
            insert.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            m_logger.error("delete proceso_programacion Error: "+sqle.toString());
            objresultado.setThereError(true);
            objresultado.setCodError(result);
            objresultado.setMsgError(msgError+" :"+sqle.toString());
        }

        return objresultado;
    }
    
    /**
     * Retorna lista con el itinerario del proceso seleccionado
     * 
     * @param _empresaId
     * @param _procesoId
     * @param _jtStartIndex
     * @param _jtPageSize
     * @param _jtSorting
     * @return 
     */
    public List<ProcesoEjecucionVO> getItinerario(String _empresaId, int _procesoId,
            int _jtStartIndex, 
            int _jtPageSize, 
            String _jtSorting){
        
        List<ProcesoEjecucionVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        ProcesoEjecucionVO data;
        
        try{
            String sql ="select "
                    + "iti.empresa_id,"
                    + "iti.proc_id,"
                    + "proceso.proc_name,"
                    + "iti.fecha_inicio_ejecucion inicio,"
                    + "iti.fecha_fin_ejecucion fin,"
                    + "iti.resultado_ejecucion,"
                    + "iti.exec_user,"
                    + "iti.depto_id,"
                    + "depto.depto_nombre,"
                    + "iti.cenco_id, "
                    + "cenco.ccosto_nombre cenco_nombre "
                + "from proceso_itinerario_ejecucion iti "
                    + "inner join proceso on (iti.proc_id = proceso.proc_id) "
                    + "left outer join departamento depto on (iti.empresa_id = depto.empresa_id and iti.depto_id = depto.depto_id) " +
                    "left outer join centro_costo cenco on (iti.depto_id = cenco.depto_id and iti.cenco_id = cenco.ccosto_id) "
                + " where 1=1 ";
           
            if (_empresaId != null && _empresaId.compareTo("") != 0){        
                sql += " and iti.empresa_id = '" +  _empresaId +"'";
            }
            
            if (_procesoId != -1){        
                sql += " and iti.proc_id = " + _procesoId;
            }
           
            sql += " order by " + _jtSorting; 
            if (_jtPageSize > 0){
                sql += " limit "+_jtPageSize + " offset "+_jtStartIndex;
            }
            System.out.println("[ProcesosDAO.getItinerario]Sql: "+sql);
            dbConn = dbLocator.getConnection(m_dbpoolName,"[ProcesosDAO.getItinerario]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new ProcesoEjecucionVO();
                data.setEmpresaId(rs.getString("empresa_id"));
                data.setProcesoId(rs.getInt("proc_id"));
                data.setProcesoName(rs.getString("proc_name"));
                data.setFechaHoraInicioEjecucion(rs.getString("inicio"));
                data.setFechaHoraFinEjecucion(rs.getString("fin"));
                data.setResultado(rs.getString("resultado_ejecucion"));
                data.setUsuario(rs.getString("exec_user"));
                data.setDeptoId(rs.getString("depto_id"));
                data.setCencoId(rs.getInt("cenco_id"));
                data.setDeptoNombre(rs.getString("depto_nombre"));
                data.setCencoNombre(rs.getString("cenco_nombre"));
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
     * @param _procesoId
     * @return 
     */
    public int getItinerarioCount(String _empresaId, int _procesoId){
        int count=0;
        try {
            dbConn = dbLocator.getConnection(m_dbpoolName,"[ProcesosDAO.getItinerarioCount]");
            Statement statement = dbConn.createStatement();
            String strSql ="SELECT count(proc_id) "
                + "FROM proceso_itinerario_ejecucion iti "
                + "where 1 = 1 ";
               
            if (_empresaId != null && _empresaId.compareTo("") != 0){        
                strSql += " and iti.empresa_id = '" +  _empresaId +"'";
            }
            
            if (_procesoId != -1){        
                strSql += " and iti.proc_id = " + _procesoId;
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
