/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.servlet.procesos;

import cl.femase.gestionweb.dao.TraspasoHistoricoDAO;
import cl.femase.gestionweb.servlet.BaseServlet;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.RegistroHistoricoVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.io.IOException;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author aledi
 */
@WebServlet(name = "TransferToHistoricalTables", urlPatterns = {"/servlet/TransferToHistoricalTables"})
public class TransferToHistoricalTables extends BaseServlet {

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
        
        HttpSession session         = request.getSession(true);
        ServletContext application  = this.getServletContext();
        PropertiesVO appProperties  = (PropertiesVO)application.getAttribute("appProperties");
        if (session!=null) session.removeAttribute("mensaje");else session = request.getSession();
        UsuarioVO userConnected     = (UsuarioVO)session.getAttribute("usuarioObj");
        
        String accion           = request.getParameter("accion");
        String empresa          = userConnected.getEmpresaId();
        String tabla            = request.getParameter("tabla");
        
        System.out.println(WEB_NAME+"-----------------------------------");
        System.out.println(WEB_NAME+"[GestionFemase."
            + "servlet.TraspasoHistorico.processRequest]"
            + "Accion: " + accion 
            + ", tabla: " + tabla
            + ", empresaId (del usuario): " + empresa);
        request.setAttribute("tabla", tabla);
        
        TraspasoHistoricoDAO historicosDao = new TraspasoHistoricoDAO();
        
        if (accion.compareTo("lastrow") == 0){
            //Mostrar ultimo registro traspasado a historico
            System.out.println(WEB_NAME+"[GestionFemase."
                + "servlet.TraspasoHistorico.processRequest]"
                + "Mostrar ultimo registro traspasado a historico");
            RegistroHistoricoVO ultimoRegistro = historicosDao.getUltimoRegistroHistorico(empresa, tabla);
            request.setAttribute("ultimoRegistro", ultimoRegistro);
        }else if (accion.compareTo("preview") == 0){
            //vista previa
            System.out.println(WEB_NAME+"[GestionFemase."
                + "servlet.TraspasoHistorico.processRequest]"
                + "Vista previa de traspaso a historico");
            String startDate    = request.getParameter("startDate");
            String endDate      = request.getParameter("endDate");
            
            RegistroHistoricoVO registro = 
                historicosDao.getVistaPreviaTraspasoHistorico(empresa, tabla, startDate, endDate);
            
            System.out.println(WEB_NAME+"[GestionFemase."
                + "servlet.TraspasoHistorico.processRequest]"
                + "Objeto Vista Previa."
                + "Tabla: " + registro.getTabla()
                + ", Num registros: " + registro.getNumRegistros());
            
            request.setAttribute("vista_previa", registro);
            request.setAttribute("startDate", startDate);
            request.setAttribute("endDate", endDate);
        }else if (accion.compareTo("execute") == 0){
            //Ejecutar traspaso a historico
            System.out.println(WEB_NAME+"[GestionFemase."
                + "servlet.TraspasoHistorico.processRequest]"
                + "Ejecutar traspaso a historico");
            String startDate    = request.getParameter("startDate");
            String endDate      = request.getParameter("endDate");
            ResultCRUDVO resultado = new ResultCRUDVO();
            int filasEliminadas = 0;
            switch (tabla){
                
                case "marca":
                    resultado = historicosDao.traspasaMarcasHistoricas(empresa, startDate, endDate);
                    break;
                case "marca_rechazo":
                    resultado = historicosDao.traspasaMarcasRechazosHistoricas(empresa, startDate, endDate);
                    break;    
                case "detalle_ausencia":
                    resultado = historicosDao.traspasaDetallesAusenciasHistoricas(empresa, startDate, endDate);
                    break;
                case "detalle_asistencia":
                    resultado = historicosDao.traspasaDetalleAsistenciaHistoricos(empresa, startDate, endDate);
                    break;    
                case "mantencion_evento":
                    resultado = historicosDao.traspasaLogEventosHistoricos(empresa, startDate, endDate);
                    break;    
            }
            
            filasEliminadas = historicosDao.eliminaRegistrosTraspasados(empresa, tabla, startDate, endDate);
            
            RegistroHistoricoVO resumen = new  RegistroHistoricoVO();
            resumen.setTabla(tabla);
            resumen.setNumRegistrosInsertados(resultado.getFilasAfectadas());
            
            System.out.println(WEB_NAME+"[GestionFemase."
                + "servlet.TraspasoHistorico.processRequest]"
                + "Objeto Resumen."
                + "Tabla: " + resumen.getTabla()
                + ", Registros insertados: " + resumen.getNumRegistrosInsertados()
                + ", filasEliminadas: " + filasEliminadas);
            
            request.setAttribute("resumen", resumen);
            request.setAttribute("startDate", startDate);
            request.setAttribute("endDate", endDate);
        }
        
        
        
        request.getRequestDispatcher("/procesos/traspaso_historicos.jsp").forward(request, response);
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
            setResponseHeaders(response);processRequest(request, response);
        }else{
            session.setAttribute("mensaje", "Sesion de usuario "+request.getParameter("username")
                +" no valida");
            System.err.println("Sesion de usuario "+request.getParameter("username")
                +" no valida");
            request.getRequestDispatcher("/mensaje.jsp").forward(request, response);
        }
    }

    /**
    * 
    * @param response
    */
    public void setResponseHeaders(final HttpServletResponse response) {
        
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
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
            setResponseHeaders(response);processRequest(request, response);
        }else{
            session.setAttribute("mensaje", "Sesion de usuario "+request.getParameter("username")
                +" no valida");
            System.err.println("Sesion de usuario "+request.getParameter("username")
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
