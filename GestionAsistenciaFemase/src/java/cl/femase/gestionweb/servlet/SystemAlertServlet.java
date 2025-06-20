/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cl.femase.gestionweb.servlet;

import cl.femase.gestionweb.business.AlertaSistemaBp;
import cl.femase.gestionweb.vo.AlertaSistemaVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class SystemAlertServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest _req, HttpServletResponse _resp) throws ServletException, IOException {
        
        HttpSession session = _req.getSession(true);
        ServletContext application = this.getServletContext();
        PropertiesVO appProperties=(PropertiesVO)application.getAttribute("appProperties");
        UsuarioVO userConnected = (UsuarioVO)session.getAttribute("usuarioObj");
        AlertaSistemaBp alertasbp = new AlertaSistemaBp(new PropertiesVO());
        
        ModalData modalData = new ModalData();
        
        // Lógica para determinar si hay alerta (ejemplo aleatorio)
        boolean hayAlerta = true;//new Random().nextBoolean(); // Simula condición dinámica
        Boolean alertsDisplayed = (Boolean)session.getAttribute("alertsDisplayed");
        if (alertsDisplayed==null) alertsDisplayed=false;
        
        System.out.println("<-- Usuario.empresa_id: " + userConnected.getEmpresaId());        
        List<AlertaSistemaVO> listaAlertasActivas = 
            alertasbp.getAlertasActivas(userConnected.getEmpresaId());
        if (listaAlertasActivas.isEmpty()) hayAlerta = false;
        
        modalData.setHasAlert(hayAlerta);
        System.out.println("<-- Ya se han mostrado alertas de Sistema? " + alertsDisplayed);
        
        if(hayAlerta && !alertsDisplayed) {
            System.out.println("<------------ [SystemAlertServlet] Mostrar Alerta de Sistema ---------->");
            AlertaSistemaVO currentAlert = listaAlertasActivas.get(0);
            
            modalData.setTitulo(currentAlert.getTitulo());
            modalData.setContenido(currentAlert.getMensaje()
                + ". Inicio: " + currentAlert.getFechaHoraDesde()
                + ", fin: " + currentAlert.getFechaHoraHasta());
            session.setAttribute("alertsDisplayed", true);
        }else{
            System.out.println("<------------ [SystemAlertServlet] NO Mostrar Alerta de Sistema ---------->");
            modalData.setHasAlert(false);
        }

        _resp.setContentType("application/json");
        _resp.setCharacterEncoding("UTF-8");
        
        String json = new Gson().toJson(modalData);
        _resp.getWriter().write(json);
    }
    
    /**
    * 
    */
    public class ModalData {
        private boolean hasAlert;
        private String titulo;
        private String contenido;

        // Getters y setters
        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }
        
        public String getContenido() { return contenido; }
        public void setContenido(String contenido) { this.contenido = contenido; }

        public boolean isHasAlert() {
            return hasAlert;
        }

        public void setHasAlert(boolean hasAlert) {
            this.hasAlert = hasAlert;
        }
        
        
    }
}
