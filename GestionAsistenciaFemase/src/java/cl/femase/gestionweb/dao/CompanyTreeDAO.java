/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.dao;

import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.vo.EmpresaVO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alexander
 */
public class CompanyTreeDAO extends BaseDAO{
    //private Connection dbConn;
    //DbDirectConnection dbconnection = DbDirectConnection.getInstance();
    
    /**
     * Retorna lista con las empresas existentes en el sistema
     * 
     * @return 
     */
    public List<EmpresaVO> getEmpresas(){
        
        List<EmpresaVO> lista = new ArrayList<>();
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        EmpresaVO data;
        
        try{
            String sql ="SELECT empresa_id, empresa_nombre, "
                + "empresa_rut, empresa_direccion, "
                + "empresa_estado "
                + " FROM empresa order by empresa_id ";
            
            dbConn = dbLocator.getConnection(m_dbpoolName,"[CompanyTreeDAO.getEmpresas]");
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()){
                data = new EmpresaVO();
                data.setId(rs.getString("empresa_id"));
                data.setNombre(rs.getString("empresa_nombre"));
                data.setRut(rs.getString("empresa_rut"));
                data.setDireccion(rs.getString("empresa_direccion"));
                //data.setRegionId(rs.getInt("region_id"));
                data.setEstadoId(rs.getInt("empresa_estado"));
                
                lista.add(data);
            }

            ps.close();
            rs.close();
            dbLocator.freeConnection(dbConn);
        }catch(SQLException|DatabaseException sqle){
            System.err.println("[Treeview.CompanyTreeDAO.getEmpresas]Error: "+sqle.toString());
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
}
