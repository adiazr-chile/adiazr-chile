/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.servlet;

import cl.femase.gestionweb.business.MaintenanceEventsBp;
import cl.femase.gestionweb.business.UsuarioBp;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author Alexander
 */
public class UserLogout extends BaseServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        PrintWriter out = response.getWriter();
        try {
             HttpSession session = request.getSession();

            if (session != null) {
                UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");

                if (userConnected != null){
                    session.removeAttribute("usuarioObj");
                    session.invalidate();
                    //marcar al usuario como No conectado
                    ServletContext application = this.getServletContext();
                    PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
                    UsuarioBp userBp    = new UsuarioBp(appProperties);
                    //serConnected.setUserConnected("N");
                    userBp.openDbConnection();
                    userBp.setConnectionStatus(userConnected);
                    userBp.closeDbConnection();
                    
                    MaintenanceEventsBp eventsBp = new MaintenanceEventsBp(appProperties);
                    
                    MaintenanceEventVO resultado=new MaintenanceEventVO();
                    resultado.setUsername(userConnected.getUsername());
                    resultado.setDatetime(new Date());
                    resultado.setUserIP(request.getRemoteAddr());
                    resultado.setType("AUT");
                    resultado.setEmpresaIdSource(userConnected.getEmpresaId());
                    resultado.setEmpresaId(userConnected.getEmpresaId());
                    resultado.setDeptoId(userConnected.getDeptoId());
                    resultado.setCencoId(userConnected.getIdCencoUsuario());

                    resultado.setDescription("Usuario desconectado");
                    eventsBp.addEvent(resultado);
                    
                
                }
            }
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        } finally { 
            out.close();
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        setResponseHeaders(response);processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        setResponseHeaders(response);processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
