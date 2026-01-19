package cl.femase.gestionweb.servlet.vacaciones;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import cl.femase.gestionweb.dao.VacacionesDAO;
import cl.femase.gestionweb.vo.VacacionesVO;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "VerInfoVacaciones", urlPatterns = {"/servlet/VerInfoVacaciones"})
public class VerInfoVacaciones extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final Gson gson = new GsonBuilder()
        .setDateFormat("yyyy-MM-dd")
        .create();

    // Ajusta a tu forma real de obtener DAO / conexiones
    private VacacionesDAO getDao() {
        return new VacacionesDAO(null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Parámetros
        String empresaId   = request.getParameter("empresaId");
        String centroCosto = request.getParameter("centroCosto");
        String[] empleados = request.getParameterValues("empleados"); // select multiple [web:100][web:106]

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        // Validaciones básicas
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

        VacacionesDAO dao = getDao();
        List<VacacionesVO> listadoDataVacacionesEmpleados = new ArrayList<>();
        try {
            
            listadoDataVacacionesEmpleados = dao.getVacacionesCalculadasEmpleados(empresaId, empleados);
            String json = gson.toJson(listadoDataVacacionesEmpleados);
            System.out.println("[VerInfoVacaciones.doPost]"
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

    // Opcional: redirigir GET a POST o devolver error
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
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
