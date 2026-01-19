/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cl.femase.gestionweb.servlet.vacaciones;

import cl.femase.gestionweb.business.CalculoVacacionesBp;
import cl.femase.gestionweb.business.CentroCostoBp;
import cl.femase.gestionweb.business.DetalleAusenciaBp;
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.common.DatabaseLocator;
import cl.femase.gestionweb.dao.VacacionesDAO;
import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.vo.CentroCostoVO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import cl.femase.gestionweb.vo.VacacionProgJsonVO;
import cl.femase.gestionweb.vo.VacacionesVO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
*
* @author aledi
*/
@WebServlet(name = "CalculoVacacionesNew", urlPatterns = {"/servlet/CalculoVacacionesNew"})
public class CalculoVacacionesNew extends BaseServlet{

    private static final long serialVersionUID = 1L;
    private final Gson gson = new GsonBuilder()
        .setDateFormat("yyyy-MM-dd")
        .create();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        if (session!=null) session.removeAttribute("mensaje");else session = request.getSession();
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        if (userConnected != null){
            setResponseHeaders(response);processRequest(request, response);
        }else{
            session.setAttribute("mensaje", "Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            System.out.println(WEB_NAME+"[NewCalculoVacacionesServlet.doGet]"
                + "Sesion de usuario " + request.getParameter("username")
                +" no valida");
            request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        if (session!=null) session.removeAttribute("mensaje");else session = request.getSession();
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        if (userConnected != null){
            setResponseHeaders(response);processRequest(request, response);
        }else{
            session.setAttribute("mensaje", "Sesion de usuario "+request.getParameter("username")
                    +" no valida");
            System.out.println(WEB_NAME+"[NewCalculoVacacionesServlet.doPost]"
                + "Sesion de usuario " + request.getParameter("username")
                +" no valida");
            request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
        }
    }
    
    /**
    * 
    * @param request
    * @param response
    * @throws ServletException
    * @throws IOException
    */
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        ServletContext application              = this.getServletContext();
        PropertiesVO appProperties              = (PropertiesVO)application.getAttribute("appProperties");
        UsuarioVO userConnected                 = (UsuarioVO)session.getAttribute("usuarioObj");
        CalculoVacacionesBp calculoVacacionesBp = new CalculoVacacionesBp(appProperties);
        DetalleAusenciaBp detAusenciaBp         = new DetalleAusenciaBp(appProperties);
        CentroCostoBp centroCostoBp         = new CentroCostoBp(appProperties);
        
        String empresaId   = request.getParameter("empresaId");
        String centroCosto = request.getParameter("centroCosto");
        String[] empleados = request.getParameterValues("empleados");  // select multiple [web:94][web:100]

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        if (empresaId == null || empresaId.isEmpty()
                || centroCosto == null || centroCosto.isEmpty()
                || empleados == null || empleados.length == 0) {

            ErrorResponse err = new ErrorResponse();
            err.setStatus("ERROR");
            err.setMessage("Parámetros inválidos: empresaId, centroCosto y al menos un empleado son obligatorios.");

            out.print(gson.toJson(err));
            out.flush();
            return;
        }

        MaintenanceEventVO resultVO = new MaintenanceEventVO();
        resultVO.setUsername(userConnected.getUsername());
        resultVO.setDatetime(new Date());
        resultVO.setUserIP(request.getRemoteAddr());
        resultVO.setType("VAC");
        resultVO.setEmpresaIdSource(userConnected.getEmpresaId());
        
//        CalculoVacacionesBp bpVacaciones = new CalculoVacacionesBp(null);
        java.sql.Connection databaseConnection = null;
        try {
            CentroCostoVO cencoData = 
                centroCostoBp.getDepartamentoByCentroCosto(empresaId, Integer.parseInt(centroCosto));
            
            DatabaseLocator dbLocator = null;
            try{
                dbLocator = DatabaseLocator.getInstance();
                databaseConnection = dbLocator.getConnection(appProperties.getDbPoolName(), "[CalculoVacacionesNew.processRequest]");
            }catch(DatabaseException dbex){
                System.err.println("[CalculoVacacionesBp.setVP_Cenco]Error:" + dbex.toString() );
            }    
            for (String runEmpleado : empleados) {
                if (runEmpleado == null) continue;
                runEmpleado = runEmpleado.trim();
                if (runEmpleado.isEmpty()) continue;
            
                System.out.println(WEB_NAME+"[CalculoVacacionesNew.processRequest]"
                    + "Calcular saldos de vacaciones "
                    + "para [empresa, rut] = [" + empresaId + "," + runEmpleado + "]. "
                    + "Invocar bp.setVBA()");
                
                //no debe invocar nada referente a vacaciones con saldo por periodos...
                ResultCRUDVO fnExec = 
                    calculoVacacionesBp.setVBA_Empleado(databaseConnection, 
                        empresaId, 
                        runEmpleado, 
                        resultVO);
                
                if (fnExec != null && fnExec.getFilasAfectadasObj() != null){
                    System.out.println(WEB_NAME + "[CalculoVacacionesNew.processRequest]"
                        + "Filas afectadas, post ejecucion de la funcion " + Constantes.fnSET_VBA_EMPLEADO
                        + ": " + fnExec.getFilasAfectadasObj().toString());
                }

                /**
                requerimiento. Vacaciones saldo vba x periodo.
                punto 3.5: Agregar un nuevo procedimiento que se encargue 
                de modificar la tabla 'vacaciones_saldo_periodo'. 

                modificar el estado de los registros en la nueva tabla 'vacaciones_saldo_periodo'.
                Si un empleado tiene más de de 2 registros en esta tabla, sólo deben quedar vigentes los últimos dos registros existentes (los más recientes). Quedando el resto en estado 'No Vigente'. En caso contrario, todos los registros existentes deben quedar como 'Vigentes'     
                **/
//                System.out.println(WEB_NAME+"[CalculoVacacionesNew.processRequest]"
//                    + "Set estado de saldos de vacaciones por periodos...");
//                ResultCRUDVO fnExec2 = 
//                    calculoVacacionesBp.setEstadoSaldosVacacionesPeriodos(empresaId, runEmpleado);

                VacacionProgJsonVO fnExec3 = 
                    calculoVacacionesBp.setVP_Empleado(databaseConnection, 
                        empresaId, runEmpleado, resultVO);
                if (fnExec3 != null && fnExec3.getAffectedRows() != null){
                    System.out.println(WEB_NAME + "[CalculoVacacionesNew.processRequest]"
                        + "Filas afectadas, post ejecucion de la funcion " + Constantes.fnSET_VP_EMPLEADO
                        + ": " + fnExec3.getAffectedRows().toString());
                }
                System.out.println(WEB_NAME+"[CalculoVacacionesNew.processRequest]"
                    + "Actualizar saldos de vacaciones "
                    + "en tabla detalle_ausencia "
                    + "Run: "+ runEmpleado);

                detAusenciaBp.actualizaSaldosVacaciones(runEmpleado);
                
            }
            
            dbLocator.freeConnection(databaseConnection);
            
            // 2) Obtener los datos ya calculados para armar la tabla
            VacacionesDAO dao = new VacacionesDAO(appProperties);
            List<VacacionesVO> listadoDataVacacionesEmpleados =
                dao.getVacacionesCalculadasEmpleados(empresaId, empleados);
            listadoDataVacacionesEmpleados.sort(Comparator.comparing(v -> v.getNombreEmpleado().toLowerCase()));
            
            String json = gson.toJson(listadoDataVacacionesEmpleados); // array JSON para DataTables [web:89][web:93]
            out.print(json);
            out.flush();

        } catch (NumberFormatException ex) {
            ex.printStackTrace();

            ErrorResponse err = new ErrorResponse();
            err.setStatus("ERROR_NUMBERFORMAT");
            err.setMessage("Error al calcular/obtener vacaciones: " + ex.getMessage());

            out.print(gson.toJson(err));
            out.flush();

        } finally {
//            if (conn != null) {
//                try { conn.close(); } catch (SQLException ignore) {}
//            }
        }
    }
    
    // Clase simple para errores JSON
    private static class ErrorResponse {
        private String status;
        private String message;
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
}
