/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.servlet;

import cl.femase.gestionweb.dao.MaintenanceEventsDAO;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
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
@WebServlet(name = "TestInsertEvent", urlPatterns = {"/TestInsertEvent"})
public class TestInsertEvent extends BaseServlet {

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
            //parametros de entrada de la funcion
            int iteraciones = Integer.parseInt(request.getParameter("iteraciones"));
            System.out.println(WEB_NAME+"[TestInsertEvent]Num iteraciones= " + iteraciones);
            for (int x = 0; x <= iteraciones; x++){
                insertEvent(request, x);
            }
        }catch(NumberFormatException ex){
            System.err.println("[TestInsertEvent]Error: " + ex.toString());
        }
    }

    /**
    * 
    */
    private void insertEvent(HttpServletRequest _request, 
            int _iteracion){
        MaintenanceEventsDAO daoEventos = new MaintenanceEventsDAO(new PropertiesVO());
        
        //***********************************************************************************************
        
        MaintenanceEventVO resultado=new MaintenanceEventVO();
        resultado.setUsername("dummy_" + _iteracion );
        resultado.setDatetime(new Date());
        resultado.setUserIP(_request.getRemoteAddr());
        resultado.setType("DUMMY");
        resultado.setEmpresaIdSource("emp99");
        resultado.setDescription("Evento dummy, it_" + _iteracion);
        
        ResultCRUDVO respuesta = daoEventos.addLogAuditoria(resultado);
        
        if (respuesta.isThereError()){
            System.err.println("[TestInsertEvent]"
                + "Error al insertar log de auditoria. "
                + "Detalle: " + respuesta.getMsgError());
        }else{
            System.out.println(WEB_NAME+"[TestInsertEvent]date:[ " 
                + new Date() + "] Inserta Log auditoria OK");
        }
        
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
