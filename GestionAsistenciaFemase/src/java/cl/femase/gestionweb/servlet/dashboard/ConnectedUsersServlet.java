/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package cl.femase.gestionweb.servlet.dashboard;

import cl.femase.gestionweb.vo.UsuarioVO;
import cl.femase.gestionweb.vo.UsuariosByPerfilVO;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author aledi
 */
@WebServlet(name = "ConnectedUsers", urlPatterns = {"/ConnectedUsers"})
public class ConnectedUsersServlet extends HttpServlet {

    /**
    * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
    * methods.
    *
    * @param _request
    * @param _response
    * @throws ServletException if a servlet-specific error occurs
    * @throws IOException if an I/O error occurs
    */
    protected void processRequest(HttpServletRequest _request, 
            HttpServletResponse _response)
            throws ServletException, IOException {
        System.out.println("[DashBoardServlet]processRequest...");
        _response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = _response.getWriter()) {
            
            ServletContext ctx = _request.getServletContext();
            ConcurrentHashMap<String, UsuarioVO> usuarios = 
            (ConcurrentHashMap<String, UsuarioVO>) ctx.getAttribute("usuariosActivos");
      
            List<UsuariosByPerfilVO> summary = contarUsuariosPorPerfil(usuarios);
            _request.setAttribute("usuarios_conectados", usuarios);
            _request.setAttribute("summary_perfiles_usuarios_conectados", summary);
            
            RequestDispatcher vista = _request.getRequestDispatcher("dashboard/usuarios_conectados.jsp");
            vista.forward(_request, _response);
        }
    }

    /**
    * 
    * @param usuariosMap
    * @return 
    */
    public List<UsuariosByPerfilVO> contarUsuariosPorPerfil(ConcurrentHashMap<String, UsuarioVO> usuariosMap) {
        return usuariosMap.values().stream()
            .collect(Collectors.groupingBy(
                UsuarioVO::getNomPerfil,           // Agrupa por nombre de perfil
                Collectors.counting()))             // Cuenta la cantidad de usuarios en cada grupo
            .entrySet().stream()
            .map(e -> new UsuariosByPerfilVO(e.getKey(), e.getValue().intValue()))
            .collect(Collectors.toList());
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        if (session!=null) session.removeAttribute("mensaje");else session = request.getSession();
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        if (userConnected != null){
            processRequest(request, response);
        }else{
            session.setAttribute("mensaje", "Sesion de usuario "+request.getParameter("username")
                + " no valida");
            System.err.println("[ConnectedUsersServlet]Sesion de usuario "+request.getParameter("username")
                +" no valida");
            request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        if (session!=null) session.removeAttribute("mensaje");else session = request.getSession();
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

        if (userConnected != null){
            processRequest(request, response);
        }else{
            session.setAttribute("mensaje", "Sesion de usuario "+request.getParameter("username")
                + " no valida");
            System.err.println("[ConnectedUsersServlet]Sesion de usuario "+request.getParameter("username")
                +" no valida");
            request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
