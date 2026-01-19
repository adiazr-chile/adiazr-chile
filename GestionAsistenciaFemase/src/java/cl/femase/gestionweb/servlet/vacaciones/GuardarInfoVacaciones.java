package cl.femase.gestionweb.servlet.vacaciones;

import cl.femase.gestionweb.business.CalculoVacacionesBp;
import cl.femase.gestionweb.business.DetalleAusenciaBp;
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.DatabaseException;
import cl.femase.gestionweb.common.DatabaseLocator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import cl.femase.gestionweb.dao.VacacionesDAO;
import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import cl.femase.gestionweb.vo.VacacionProgJsonVO;
import cl.femase.gestionweb.vo.VacacionesVO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebServlet(name = "GuardarInfoVacaciones", urlPatterns = {"/servlet/GuardarInfoVacaciones"})
public class GuardarInfoVacaciones extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final Gson gson = new GsonBuilder()
        .setDateFormat("yyyy-MM-dd")
        .create();

    // Ajusta a tu forma real de obtener DAO / conexiones
    private VacacionesDAO getDao() {
        return new VacacionesDAO(null);
    }

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
    */
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        ServletContext application              = this.getServletContext();
        PropertiesVO appProperties              = (PropertiesVO)application.getAttribute("appProperties");
        UsuarioVO userConnected                 = (UsuarioVO)session.getAttribute("usuarioObj");
        DetalleAusenciaBp detAusenciaBp         = new DetalleAusenciaBp(appProperties);
        
        // Parámetros
        String empresaId        = request.getParameter("empresaId");
        String runEmpleado      = request.getParameter("runEmpleado");
        String afpCertificado   = request.getParameter("afpCertificado");
        String fechaEmisionCertificadoVacacionesProgresivas = request.getParameter("fecEmisionCert");
        String numCotizCert     = request.getParameter("numCotizCert");
        String diasEspeciales   = request.getParameter("diasEspeciales");
        String diasAdicionales  = request.getParameter("diasAdicionales");
        
        System.out.println(WEB_NAME + "[GuardarInfoVacaciones.processRequest]"
            + "Datos recibidos. "
            + "empresaId: " + empresaId
            + ", runEmpleado: " + runEmpleado
            + ", afpCertificado: " + afpCertificado
            + ", fechaEmisionCertificadoVacacionesProgresivas: " + fechaEmisionCertificadoVacacionesProgresivas
            + ", numCotizCert: " + numCotizCert
            + ", diasEspeciales: " + diasEspeciales
            + ", diasAdicionales: " + diasAdicionales);
        
        if (afpCertificado.compareTo("NINGUNA") == 0 || afpCertificado.compareTo("-1") == 0)
        {
            afpCertificado=null;
            fechaEmisionCertificadoVacacionesProgresivas = null;
            numCotizCert = "0";
        }
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        // Validaciones básicas
        if (empresaId == null || empresaId.isEmpty()
                || runEmpleado == null || runEmpleado.isEmpty()) {

            ErrorResponse err = new ErrorResponse();
            err.setStatus("ERROR");
            err.setMessage("Parámetros incompletos: empresaId y runEmpleado son obligatorios.");

            out.print(gson.toJson(err));
            out.flush();
            return;
        }

        List<VacacionesVO> listadoDataVacacionesEmpleado = new ArrayList<>();
        VacacionesDAO dao = getDao();
        CalculoVacacionesBp calculoVacacionesBp = new CalculoVacacionesBp(appProperties);
        try {
            MaintenanceEventVO eventoAuditoria = new MaintenanceEventVO();
            eventoAuditoria.setUsername(userConnected.getUsername());
            eventoAuditoria.setDatetime(new Date());
            eventoAuditoria.setUserIP(request.getRemoteAddr());
            eventoAuditoria.setType("VAC");
            eventoAuditoria.setEmpresaIdSource(userConnected.getEmpresaId());
            
            System.out.println(WEB_NAME + "[GuardarInfoVacaciones.processRequest]"
                + "Ejecutar funcion " + Constantes.fnSET_FECHA_BASE_VP);
            if (afpCertificado != null){ 
                ResultCRUDVO fnExec = calculoVacacionesBp.setFechaBaseVP(empresaId, 
                    runEmpleado, 
                    fechaEmisionCertificadoVacacionesProgresivas, 
                    Integer.parseInt(numCotizCert), 
                    diasEspeciales, 
                    Double.parseDouble(diasAdicionales), 
                    afpCertificado, 
                    eventoAuditoria);
                System.out.println(WEB_NAME + "[GuardarInfoVacaciones.processRequest]"
                    + "Filas afectadas, post ejecucion de la funcion " + Constantes.fnSET_FECHA_BASE_VP
                    + ": " + fnExec.getFilasAfectadasObj().toString());
            }else{
                System.out.println(WEB_NAME + "[GuardarInfoVacaciones.processRequest]"
                    + "Setear dias especiales y dias adicionales de la tabla vacaciones");
                ResultCRUDVO resultado = dao.setParcialData(empresaId, 
                    runEmpleado,
                    diasEspeciales, 
                    Double.parseDouble(diasAdicionales));
                System.out.println(WEB_NAME + "[GuardarInfoVacaciones.processRequest]"
                    + "Filas afectadas por Update 'vacaciones': " + 
                    resultado.getFilasAfectadasObj().toString());
            }
            
            System.out.println(WEB_NAME + "[GuardarInfoVacaciones.processRequest]"
                + "Ejecutar calculo de vacaciones...");
            
            calculaVacaciones(appProperties, 
                eventoAuditoria, 
                calculoVacacionesBp, 
                detAusenciaBp, 
                empresaId, 
                runEmpleado);
            
            //refrescar data de vacaciones del empleado
            String[] rutsEmpleados = {runEmpleado};
            listadoDataVacacionesEmpleado = dao.getVacacionesCalculadasEmpleados(empresaId, rutsEmpleados);
            if (listadoDataVacacionesEmpleado == null ||
                listadoDataVacacionesEmpleado.isEmpty()) {

                ErrorResponse err = new ErrorResponse();
                err.setStatus("SIN_DATOS");
                err.setMessage("No se encontraron datos de vacaciones para el empleado.");

                out.print(gson.toJson(err));
                out.flush();
                return;
            }
            
            String json = gson.toJson(listadoDataVacacionesEmpleado.get(0));
            System.out.println("[GuardarInfoVacaciones.processRequest]"
                + "Json String: " + json);
                
            out.print(json);           // devuelve array JSON para DataTables [web:89][web:93]
            out.flush();

        } catch (Exception ex) {
            ex.printStackTrace();

            ErrorResponse err = new ErrorResponse();
            err.setStatus("ERROR_DB");
            err.setMessage("Error al obtener información de vacaciones: " + ex.getMessage());

            out.print(gson.toJson(err));
            out.flush();

        } finally {
            
        }
    }

    /**
    * 
    */
    private void calculaVacaciones(PropertiesVO _appProperties,
            MaintenanceEventVO _eventoAuditoria, 
            CalculoVacacionesBp _calculoVacacionesBp,
            DetalleAusenciaBp _detAusenciaBp,
            String _empresaId, 
            String _runEmpleado){
        
        DatabaseLocator dbLocator = null;
        java.sql.Connection databaseConnection = null;
        try{
            dbLocator = DatabaseLocator.getInstance();
            databaseConnection = dbLocator.getConnection(_appProperties.getDbPoolName(), 
                "[GuardarInfoVacaciones.calculaVacaciones]");
        }catch(DatabaseException dbex){
            System.err.println("[GuardarInfoVacaciones.calculaVacaciones]Error:" + dbex.toString() );
        }
        ResultCRUDVO fnExec = 
            _calculoVacacionesBp.setVBA_Empleado(databaseConnection, 
                _empresaId, 
                _runEmpleado, 
                _eventoAuditoria);
                
        if (fnExec != null && fnExec.getFilasAfectadasObj() != null){
            System.out.println(WEB_NAME + "[GuardarInfoVacaciones.calculaVacaciones]"
                + "Filas afectadas, post ejecucion de la funcion " + Constantes.fnSET_VBA_EMPLEADO
                + ": " + fnExec.getFilasAfectadasObj().toString());
        }

        VacacionProgJsonVO fnExec3 = 
            _calculoVacacionesBp.setVP_Empleado(databaseConnection, 
                _empresaId, _runEmpleado, _eventoAuditoria);
        if (fnExec3 != null && fnExec3.getAffectedRows() != null){
            System.out.println(WEB_NAME + "[GuardarInfoVacaciones.calculaVacaciones]"
                + "Filas afectadas, post ejecucion de la funcion " + Constantes.fnSET_VP_EMPLEADO
                + ": " + fnExec3.getAffectedRows().toString());
        }
        System.out.println(WEB_NAME+"[GuardarInfoVacaciones.calculaVacaciones]"
            + "Actualizar saldos de vacaciones "
            + "en tabla detalle_ausencia "
            + "Run: "+ _runEmpleado);

        _detAusenciaBp.actualizaSaldosVacaciones(_runEmpleado);
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
