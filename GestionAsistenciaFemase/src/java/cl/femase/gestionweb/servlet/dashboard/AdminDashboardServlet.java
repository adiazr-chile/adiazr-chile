/*
* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
* Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
*/
package cl.femase.gestionweb.servlet.dashboard;

import cl.femase.gestionweb.business.DetalleAusenciaBp;
import cl.femase.gestionweb.vo.AgrupadoAusenciaVO;
import cl.femase.gestionweb.vo.DetalleAusenciaVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioCentroCostoVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import cl.femase.gestionweb.vo.UsuariosByPerfilVO;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
*
* @author aledi
*/
@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/AdminDashboardServlet"})
public class AdminDashboardServlet extends HttpServlet {

    /**
    * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
    * methods.
    *
    * @param _request
    * @param _response
     * @param _usuarioConectado
    * @throws ServletException if a servlet-specific error occurs
    * @throws IOException if an I/O error occurs
    */
    protected void processRequest(HttpServletRequest _request, 
            HttpServletResponse _response, 
            UsuarioVO _usuarioConectado)
            throws ServletException, IOException {
        System.out.println("[DashBoardServlet]processRequest. "
            + "Empresa del usuario conectado: " + _usuarioConectado.getEmpresaId());
        _response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = _response.getWriter()) {
            
            //data para usuarios conectados
            ServletContext ctx = _request.getServletContext();
            ConcurrentHashMap<String, UsuarioVO> usuarios = 
                (ConcurrentHashMap<String, UsuarioVO>) ctx.getAttribute("usuariosActivos");
            /**
            * Solo usuarios de la empresa del usuario conectado
            */
            ArrayList<UsuarioVO> usuariosEmpresa = new ArrayList<>();
            for (Map.Entry<String, UsuarioVO> entry : usuarios.entrySet()) {
                UsuarioVO user = entry.getValue();
                if (user.getEmpresaId() != null 
                        && user.getEmpresaId().compareTo(_usuarioConectado.getEmpresaId()) == 0){
                    usuariosEmpresa.add(user);
                }else if (user.getEmpresaId() == null){
                    usuariosEmpresa.add(user);
                }
            }
            
            List<UsuariosByPerfilVO> summary = contarUsuariosPorPerfil(usuariosEmpresa);
            
            _request.setAttribute("usuarios_conectados", usuarios);
            _request.setAttribute("summary_perfiles_usuarios_conectados", summary);
            
            List<UsuarioCentroCostoVO> cencosUsuario = _usuarioConectado.getCencos();
            //set data de ausencias
            DetalleAusenciaBp ausenciasBp = new DetalleAusenciaBp(new PropertiesVO());
            LinkedHashMap<Integer, DetalleAusenciaVO> ausenciasHoy = ausenciasBp.getAusenciasHoy(_usuarioConectado.getEmpresaId(), cencosUsuario);
            List<AgrupadoAusenciaVO> summaryAusencias = contarAusencias(ausenciasHoy);
            _request.setAttribute("empleados_ausencias", ausenciasHoy);
            _request.setAttribute("summary_ausencias", summaryAusencias);
            RequestDispatcher vista = _request.getRequestDispatcher("dashboard/admin_dashboard_01.jsp");
            vista.forward(_request, _response);
        }
    }

    /**
    * 
    * @param usuariosList
    * @return 
    */
    public List<UsuariosByPerfilVO> contarUsuariosPorPerfil(ArrayList<UsuarioVO> usuariosList) {
        return usuariosList.stream()
            .collect(Collectors.groupingBy(
                UsuarioVO::getNomPerfil,        // Agrupa por nombre de perfil
                Collectors.counting()))         // Cuenta la cantidad de usuarios en cada grupo
            .entrySet().stream()
            .map(e -> new UsuariosByPerfilVO(e.getKey(), e.getValue().intValue()))
            .collect(Collectors.toList());
    }

//    public List<UsuariosByPerfilVO> contarUsuariosPorPerfil(ConcurrentHashMap<String, UsuarioVO> usuariosMap) {
//        return usuariosMap.values().stream()
//            .collect(Collectors.groupingBy(
//                UsuarioVO::getNomPerfil,           // Agrupa por nombre de perfil
//                Collectors.counting()))             // Cuenta la cantidad de usuarios en cada grupo
//            .entrySet().stream()
//            .map(e -> new UsuariosByPerfilVO(e.getKey(), e.getValue().intValue()))
//            .collect(Collectors.toList());
//    }
    
    /**
    * 
    * @param _ausencias
    * @return 
    */
    public List<AgrupadoAusenciaVO> contarAusencias(LinkedHashMap<Integer, DetalleAusenciaVO> _ausencias) {
        return _ausencias.values().stream()
            .collect(Collectors.groupingBy(DetalleAusenciaVO::getIdAusencia))
            .entrySet().stream()
            .map(entry -> {
                Integer id = entry.getKey();
                List<DetalleAusenciaVO> items = entry.getValue();
                String nombre = items.get(0).getNombreAusencia(); // toma el nombre del primero
                Long count = (long) items.size();
                return new AgrupadoAusenciaVO(id, nombre, count); // Constructor ajustado
            })
            .collect(Collectors.toList());
    }

//    public List<AgrupadoAusenciaVO> contarAusencias(LinkedHashMap<Integer, DetalleAusenciaVO> _ausencias){
//        List<AgrupadoAusenciaVO> agrupados = _ausencias.values().stream()
//            .collect(Collectors.groupingBy(
//                DetalleAusenciaVO::getIdAusencia, Collectors.counting()
//            ))
//            .entrySet().stream()
//            .map(e -> new AgrupadoAusenciaVO(e.getKey(), e.getValue()))
//            .collect(Collectors.toList());
//        return agrupados;
//    }
    
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
            processRequest(request, response, userConnected);
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
            processRequest(request, response, userConnected);
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
