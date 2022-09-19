/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.servlet;

import cl.femase.gestionweb.business.MaintenanceEventsBp;
import cl.femase.gestionweb.common.GetPropertyValues;
import cl.femase.gestionweb.vo.PropertiesVO;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexander
 */
public class LoadParams extends BaseServlet {

    protected static Logger m_logger = Logger.getLogger("gestionfemase");
    GetPropertyValues m_properties = new GetPropertyValues();
    public PropertiesVO appProperties;
    
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

     @Override
   public void init() {
	m_logger.debug("Cargando parametros en sesion");
        System.out.println(WEB_NAME+"[GestionFemase.LoadParams.init]Cargando parametros en sesion...");
        appProperties=new PropertiesVO();
        
        Calendar currentCalendar = Calendar.getInstance(new Locale("es","CL"));
        SimpleDateFormat yearFmt = new SimpleDateFormat("yyyy");
        appProperties.setVersion(m_properties.getKeyValue("version"));
        appProperties.setStartYear(m_properties.getKeyValue("launchYear"));
        appProperties.setCurrentYear(yearFmt.format(currentCalendar.getTime()));
        
        System.out.println(WEB_NAME+"[GestionFemase.LoadParams.init]Version del Sistema: " + appProperties.getVersion());
        
//        appProperties.setCalendarWSEndPoint(m_properties.getKeyValue("calendarWSEndPoint"));
        appProperties.setMailHost(m_properties.getKeyValue("mailHost"));
        appProperties.setMailPort(m_properties.getKeyValue("mailPort"));
        appProperties.setMailFrom(m_properties.getKeyValue("mailFrom"));

        //direcciones de email separadas por coma (,)
        StringTokenizer tokenMails = new StringTokenizer(m_properties.getKeyValue("mailTo"), ",");

        int pos = 0;
        String[] mailTo = new String[tokenMails.countTokens()];
        while (tokenMails.hasMoreElements()) {
            mailTo[pos] = tokenMails.nextToken();
            pos++;
        }
        appProperties.setMailTo(mailTo);
        appProperties.setMailSubject(m_properties.getKeyValue("mailSubject"));
        appProperties.setMailBody(m_properties.getKeyValue("mailBody"));
        appProperties.setMailUsername(m_properties.getKeyValue("mailUsuario"));
        appProperties.setMailPassword(m_properties.getKeyValue("mailPassword"));
                
         //direcciones de email separadas por coma (,)
         StringTokenizer tokenMailsAdmin = new StringTokenizer(m_properties.getKeyValue("mailToAdmin"), ",");
         pos = 0;
         String[] mailToOperaciones = new String[tokenMailsAdmin.countTokens()];
         while (tokenMailsAdmin.hasMoreElements()) {
             mailToOperaciones[pos] = tokenMailsAdmin.nextToken();
             pos++;
         }
        appProperties.setMailToOperaciones(mailToOperaciones);
                
        ServletContext application = this.getServletContext();
        MaintenanceEventsBp eventsBp    = new MaintenanceEventsBp(appProperties);
                
        String pathFiles  = m_properties.getKeyValue("pathExportedFiles");
        appProperties.setPathExportedFiles(pathFiles);
        appProperties.setImagesPath(m_properties.getKeyValue("imagesPath"));
        appProperties.setDbPoolName(m_properties.getKeyValue("dbpoolname"));
        appProperties.setUploadsPath(m_properties.getKeyValue("uploadsPath"));
        appProperties.setReportesPath(m_properties.getKeyValue("reportesPath"));
        System.out.println(WEB_NAME+"[GestionFemase.LoadParams.init]"
            + "reportesPath= "+appProperties.getReportesPath()
            + ",uploadsPath= "+appProperties.getUploadsPath()
            + ",imagesPath= "+appProperties.getImagesPath());
        
        //ServletContext application = this.getServletContext();
        application.removeAttribute("appProperties");
        application.setAttribute("appProperties", appProperties);

        application.removeAttribute("exportFilesProperties");
        
          //Hora apertura y cierre de mercado
//////        ClientHorarioMercadoWS horariobec=new ClientHorarioMercadoWS();
//////        SimpleDateFormat horaformat=new SimpleDateFormat("HH:mm:ss");
//////        
//////        appProperties.setHoraAperturaMercado(horaformat.format(horariobec.getAperturaMercado()));
//////        appProperties.setHoraCierreMercado(horaformat.format(horariobec.getCierreMercado()));
         
//        appProperties.setXlsTemplatesPath(m_properties.getKeyValue("XLStemplatesPath"));
        
        //ServletContext application = this.getServletContext();
        application.removeAttribute("appProperties");
        application.setAttribute("appProperties", appProperties);
        application.removeAttribute("exportFilesProperties");
       
        System.out.println(WEB_NAME+"[GestionFemase.LoadParams.init]Gestion WEB deploy completed...");
    }
   
}
