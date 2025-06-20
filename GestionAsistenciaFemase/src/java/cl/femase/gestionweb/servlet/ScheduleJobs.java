/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.servlet;

import cl.femase.gestionweb.business.ProcesosBp;
import cl.femase.gestionweb.vo.PropertiesVO;
import jakarta.servlet.http.HttpServletRequest; 
        
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;


/**
 *
 * @author Alexander
 */
public class ScheduleJobs extends BaseServlet {

    @Override
    public void init() {
	System.out.println(WEB_NAME+"[GestionFemase.ScheduleJobs."
            + "init]Programando procesos...");
        try {
            ServletContext application = this.getServletContext();
            PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
            ProcesosBp procesosBp = new ProcesosBp(appProperties);
            procesosBp.scheduleJobs();
        }catch ( Exception ex) {
            System.err.println("[GestionFemase.ScheduleJobs.init]"
                + "Error 2 al schedular procesos: "+ex.toString());
            //throw new IOException(de.getMessage());
        }
        
    }
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            
        }catch ( Exception ex) {
            System.err.println("Error: "+ex.toString());
        }
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
        setResponseHeaders(response);processRequest(request, response);
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
        setResponseHeaders(response);processRequest(request, response);
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
