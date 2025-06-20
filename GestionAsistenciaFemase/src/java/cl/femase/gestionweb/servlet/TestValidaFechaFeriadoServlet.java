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
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author aledi
 */
@WebServlet(name = "TestValidaFechaFeriadoServlet", urlPatterns = {"/TestValidaFechaFeriadoServlet"})
public class TestValidaFechaFeriadoServlet extends BaseServlet {

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
            System.out.println(WEB_NAME+"[TestValidaFechaFeriadoServlet]Entrando...");
            //parametros de entrada de la funcion
            String startDate    = request.getParameter("startDate");
            String endDate      = request.getParameter("endDate");
            String empresaId    = request.getParameter("empresaId");
            int cencoId         = Integer.parseInt(request.getParameter("cencoId"));
            String runEmpleado = request.getParameter("runEmpleado");
            int iteraciones = Integer.parseInt(request.getParameter("iteraciones"));
            for (int x = 0; x <= iteraciones; x++){
                invokeFunction(empresaId, cencoId, runEmpleado, startDate, endDate);
            }
        }catch(NumberFormatException ex){
            System.err.println("[TestValidaFechaFeriadoServlet]Error: " + ex.toString());
        }
    }

    /**
    * 
    */
    private void invokeFunction(String _empresaId, 
            int _cencoId, 
            String _runEmpleado, 
            String _startDate,
            String _endDate){
        CalendarioFeriadoDAO daoFeriados = new CalendarioFeriadoDAO(new PropertiesVO());
        
        //***********************************************************************************************
        System.out.println(WEB_NAME+"[TestValidaFechaFeriadoServlet]date:[ " 
            + new Date() + "] INICIO invocar funcion validaFechaFeriado en un solo SQL...");
        String strFechasJson = daoFeriados.getValidaFechasJson(_empresaId, _cencoId, _runEmpleado, _startDate, _endDate);
        System.out.println(WEB_NAME+"[TestValidaFechaFeriadoServlet]"
            + "retorno JSON: " + strFechasJson);
        System.out.println(WEB_NAME+"[TestValidaFechaFeriadoServlet]date:[ " 
            + new Date() + "] INICIO invocar funcion validaFechaFeriado en un solo SQL...");
        //***********************************************************************************************
        System.out.println(WEB_NAME+"[TestValidaFechaFeriadoServlet]date:[ " 
            + new Date() + "] INICIO invocando funcion validaFechaFeriado fecha x fecha...");
        String[] fechas = Utilidades.getFechas(_startDate, _endDate);
        for (int x = 0; x < fechas.length ; x++){
            System.out.println(WEB_NAME+"[TestValidaFechaFeriadoServlet]"
                + "invocando funcion validaFechaFeriado con fecha : " + fechas[x]);
            String strRetorno = daoFeriados.esFeriadoJson(_empresaId, _cencoId, _runEmpleado, fechas[x]);
            System.out.println(WEB_NAME+"[TestValidaFechaFeriadoServlet]"
                + "retorno JSON: " + strRetorno);
        }
        System.out.println(WEB_NAME+"[TestValidaFechaFeriadoServlet]date:[ " 
            + new Date() + "] FIN invocando funcion validaFechaFeriado fecha x fecha...");
        //***********************************************************************************************
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
