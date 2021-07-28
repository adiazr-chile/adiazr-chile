/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.servlet;

import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.dao.CalendarioFeriadoDAO;
import cl.femase.gestionweb.vo.PropertiesVO;
import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author aledi
 */
@WebServlet(name = "TestValidaFechaFeriadoServlet", urlPatterns = {"/TestValidaFechaFeriadoServlet"})
public class TestValidaFechaFeriadoServlet extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        try {
            System.out.println("[TestValidaFechaFeriadoServlet]Entrando...");
            CalendarioFeriadoDAO daoFeriados = new CalendarioFeriadoDAO(new PropertiesVO());
            
            //parametros de entrada de la funcion
            String startDate    = request.getParameter("startDate");
            String endDate      = request.getParameter("endDate");
            String empresaId    = request.getParameter("empresaId");
            int cencoId         = Integer.parseInt(request.getParameter("cencoId"));
            String runEmpleado = request.getParameter("runEmpleado");
            
            //***********************************************************************************************
            System.out.println("[TestValidaFechaFeriadoServlet]date:[ " 
                + new Date() + "] INICIO invocar funcion validaFechaFeriado en un solo SQL...");
            String strFechasJson = daoFeriados.getValidaFechasJson(empresaId, cencoId, runEmpleado, startDate, endDate);
            System.out.println("[TestValidaFechaFeriadoServlet]"
                + "retorno JSON: " + strFechasJson);
            System.out.println("[TestValidaFechaFeriadoServlet]date:[ " 
                + new Date() + "] INICIO invocar funcion validaFechaFeriado en un solo SQL...");
            //***********************************************************************************************
            System.out.println("[TestValidaFechaFeriadoServlet]date:[ " 
                + new Date() + "] INICIO invocando funcion validaFechaFeriado fecha x fecha...");
            String[] fechas = Utilidades.getFechas(startDate, endDate);
            for (int x = 0; x < fechas.length ; x++){
                System.out.println("[TestValidaFechaFeriadoServlet]"
                    + "invocando funcion validaFechaFeriado con fecha : " + fechas[x]);
                String strRetorno = daoFeriados.esFeriadoJson(empresaId, cencoId, runEmpleado, fechas[x]);
                System.out.println("[TestValidaFechaFeriadoServlet]"
                    + "retorno JSON: " + strRetorno);
            }
            System.out.println("[TestValidaFechaFeriadoServlet]date:[ " 
                + new Date() + "] FIN invocando funcion validaFechaFeriado fecha x fecha...");
            //***********************************************************************************************
        }catch(Exception ex){
            System.err.println("[TestValidaFechaFeriadoServlet]Error: " + ex.toString());
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
        processRequest(request, response);
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
        processRequest(request, response);
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
