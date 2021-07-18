/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.servlet;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author aledi
 */
@WebServlet(name = "ShowPdfServlet", urlPatterns = {"/ShowPdfServlet"})
public class ShowPdfServlet extends HttpServlet {

    String FULLPDFPATH = "C:/paso/admin_CENIM PEÑALOLEN MACUL_todos.pdf";
    
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
        
        System.err.println("[ShowPdfServlet.processRequest]"
            + "Mostrar PDF...");
        response.setContentType("application/pdf");
        response.setHeader("Content-disposition","attachment;filename="+ "testPDF.pdf");
        try {
            File f = new File(FULLPDFPATH);
            FileInputStream fis = new FileInputStream(f);
            DataOutputStream os = new DataOutputStream(response.getOutputStream());
            response.setHeader("Content-Length",String.valueOf(f.length()));
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fis.read(buffer)) >= 0) {
                os.write(buffer, 0, len);
            }
        } catch (IOException e) {
            System.err.println("[ShowPdfServlet.processRequest]"
                + "Error: " + e.toString());
            e.printStackTrace();
        }
    
//            response.setContentType("text/html;charset=UTF-8");
//            String scheme       = request.getScheme();             // http
//            String serverName   = request.getServerName();     // hostname.com
//            int serverPort      = request.getServerPort();        // 80
//            String contextPath  = request.getContextPath();   // /mywebapp
//            String servletPath  = request.getServletPath();   // /servlet/MyServlet
//
//            String fullUrl = scheme + "://" + serverName + ":" + serverPort + "/" + contextPath + "/" + servletPath;
//
//            try {
//                //***********************************************************************
//                //********* NEW *********************************************************
//                // reads input file from an absolute path
//
//
//                File downloadFile = new File(FILEPATH);
//                FileInputStream inStream = new FileInputStream(downloadFile);
//
//                // if you want to use a relative path to context root:
//                String relativePath = getServletContext().getRealPath("");
//                System.out.println("[ShowPdfServlet.processRequest]"
//                    + "relativePath = " + relativePath);
//
//                // obtains ServletContext
//                ServletContext context = getServletContext();
//
//                // gets MIME type of the file
//                String mimeType = context.getMimeType(FILEPATH);
//                if (mimeType == null) {        
//                    // set to binary type if MIME mapping not found
//                    mimeType = "application/octet-stream";
//                }
//                System.out.println("[ShowPdfServlet.processRequest]"
//                    + "MIME type: " + mimeType);
//
//                // modifies response
//                response.setContentType(mimeType);
//                response.setContentLength((int) downloadFile.length());
//
//                // forces download
//                String headerKey = "Content-Disposition";
//                String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
//                response.setHeader(headerKey, headerValue);
//
//                // obtains response's output stream
//                OutputStream outStream = response.getOutputStream();
//
//                byte[] buffer = new byte[4096];
//                int bytesRead = -1;
//
//                while ((bytesRead = inStream.read(buffer)) != -1) {
//                    outStream.write(buffer, 0, bytesRead);
//                }
//
//                inStream.close();
//                outStream.close();     
//
//                System.out.println("[ShowPdfServlet.processRequest]"
//                    + "Mostrando PDF");
//            }catch(Exception ex){
//
//            }
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
