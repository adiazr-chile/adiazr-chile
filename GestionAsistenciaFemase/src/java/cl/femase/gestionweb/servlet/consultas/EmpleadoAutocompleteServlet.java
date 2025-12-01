/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package cl.femase.gestionweb.servlet.consultas;

import cl.femase.gestionweb.dao.EmpleadosDAO;
import cl.femase.gestionweb.vo.EmpleadoVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import com.google.gson.Gson;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
*
* @author aledi
*/
@WebServlet("/api/empleados")
public class EmpleadoAutocompleteServlet extends HttpServlet {

    /**
    * 
    * @param req
    * @param resp
    * @throws java.io.IOException
    */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String term = req.getParameter("term");
//        resp.setContentType("application/json; charset=UTF-8");
//        resp.setCharacterEncoding("UTF-8");
        
        HttpSession session = req.getSession(true);
        if (session!=null) session.removeAttribute("mensaje");else session = req.getSession();
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        if (userConnected != null){
            EmpleadosDAO daoEmpleados = new EmpleadosDAO(null);
            try {
                System.out.println("[EmpleadoAutocompleteServlet."
                    + "doGet]term: " + term 
                    + ", empresaId: " + userConnected.getEmpresaId());
                List<EmpleadoVO> filtrados = (term != null && !term.trim().isEmpty())
                    ? daoEmpleados.buscarEmpleadosPorFiltro(userConnected.getEmpresaId(),
                        userConnected.getCencos(), term)
                    : new ArrayList<>();
               
                Gson gson = new Gson();
                String json = gson.toJson(filtrados);

                resp.setContentType("application/json; charset=UTF-8");
                resp.getWriter().print(json);
            } catch (SQLException ex) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DB error");
            } finally {
            
            }
        }else{
            session.setAttribute("mensaje", "Sesion de usuario "+req.getParameter("username")
                +" no valida");
            try{
                req.getRequestDispatcher("/mensaje.jsp").forward(req, resp);
            }catch(ServletException sex){
                System.err.println("[EmpleadoAutocompleteServlet]Error: " + sex.toString());
            }
        }
    }

}

