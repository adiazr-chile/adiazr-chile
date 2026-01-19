/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package cl.femase.gestionweb.servlet.consultas;

import cl.femase.gestionweb.dao.CentroCostoDAO;
import cl.femase.gestionweb.vo.CentroCostoVO;
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
@WebServlet("/api/centros_costo")
public class CentrosDeCostoServlet extends HttpServlet {

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
            CentroCostoDAO daoCencos = new CentroCostoDAO(null);
            try {
                System.out.println("[CentrosDeCostoServlet."
                    + "doGet]term: " + term 
                    + ", empresaId: " + userConnected.getEmpresaId());
                List<CentroCostoVO> filtrados = (term != null && !term.trim().isEmpty())
                    ? daoCencos.buscarCentrosCostoPorFiltro(userConnected, term)
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
                System.err.println("[CentrosDeCostoServlet]Error: " + sex.toString());
            }
        }
    }

}

