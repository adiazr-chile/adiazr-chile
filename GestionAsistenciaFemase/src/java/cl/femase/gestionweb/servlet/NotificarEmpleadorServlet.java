/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cl.femase.gestionweb.servlet;

import cl.femase.gestionweb.business.MaintenanceEventsBp;
import cl.femase.gestionweb.business.NotificacionBp;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.NotificacionSolicitudClaveVO;
import cl.femase.gestionweb.vo.NotificacionVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.io.IOException;
import java.util.Date;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class NotificarEmpleadorServlet extends BaseServlet {
    
    @Override
    protected void doPost(HttpServletRequest _request, HttpServletResponse _response) throws ServletException, IOException {
        
        HttpSession session         = _request.getSession(true);
        ServletContext application  = this.getServletContext();
        PropertiesVO appProperties  = (PropertiesVO)application.getAttribute("appProperties");
        UsuarioVO userConnected     = (UsuarioVO)session.getAttribute("usuarioObj");
        String emailNotificar       = _request.getParameter("emailNotificacion");
        String empresaId            = userConnected.getEmpresaId();
        String nombreEmpleador      = _request.getParameter("nombreEmpleador");
                
        System.out.println(WEB_NAME+"[NotificarEmpleadorServlet]"
            + "nombre empleador: " + nombreEmpleador
            + ", email de notificacion: " + emailNotificar);
        
        System.out.println(WEB_NAME+"[RequestAccessCodeServlet]"
            + "Insertar registro en tabla 'notificacion'");
        
        notificaEmpleador(appProperties, 
                empresaId, 
                "Notificacion Empleador: " + nombreEmpleador, 
                _request, 
                userConnected.getUsername(),
                emailNotificar);
            MaintenanceEventsBp eventosBp   = new MaintenanceEventsBp(appProperties);
            
            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(userConnected.getUsername());
            resultado.setDatetime(new Date());
            resultado.setUserIP(_request.getRemoteAddr());
            resultado.setType("NOT");
            resultado.setEmpresaIdSource(empresaId);
            resultado.setDescription("Notificacion Empleador: " + nombreEmpleador);
            
            eventosBp.addEvent(resultado);
            _request.getRequestDispatcher("/quick_menu/quick_menu.jsp").forward(_request, _response);
                
    }
    
    /**
    * 
    * 
    */
    private NotificacionSolicitudClaveVO notificaEmpleador(
            PropertiesVO _appProperties,
            String _empresaId,
            String _evento,
            HttpServletRequest _request,
            String _username,
            String _emailNotificar){
    
        NotificacionSolicitudClaveVO evento = new NotificacionSolicitudClaveVO();
                
        String fromLabel        = "Gestion asistencia";
        String fromMail         = _appProperties.getMailFrom();
        String asuntoMail       = "Sistema de Gestion-Inicia procedimiento de "
            + "fiscalización laboral";
        String mailTo           = _emailNotificar;
                
        String mensaje = "Inicia procedimiento de "
            + "fiscalización laboral.";
        
        String mailBody = "<html>" +
            "<body>" +
            "<p>Se informa a usted que, de acuerdo con las facultades y "
            + "obligaciones legales contenidas en el Código del Trabajo y "
            + "sus leyes complementarias; en el D.F.L. Nº2 de 1967, "
            + "del Ministerio del Trabajo y Previsión Social, "
            + "y en otras disposiciones reglamentarias, se está iniciando "
            + "un procedimiento de fiscalización laboral.</p>" 
            + "<p>Atentamente,<br>" +
            "FEMASE<br>" +
            "</body>" +
            "</html>";
        
        evento.setTipoEvento("Fiscalización laboral");
        evento.setMensajeFinal(mensaje);
        
        MaintenanceEventVO resultado=new MaintenanceEventVO();
        resultado.setUsername(_username);
        resultado.setDatetime(new Date());
        resultado.setUserIP(_request.getRemoteAddr());
        resultado.setType("NOT");
        resultado.setEmpresaId(_empresaId);
        
        NotificacionBp notificacionBp=new NotificacionBp(null);
        NotificacionVO notificacion = new NotificacionVO();
        notificacion.setEmpresaId(_empresaId);
        notificacion.setMailFrom(fromMail);
        notificacion.setMailTo(mailTo);
        notificacion.setMailSubject(asuntoMail);
        notificacion.setMailBody(mailBody);
        notificacion.setUsername(_username);
        notificacion.setComentario(_evento);
        notificacionBp.insert(notificacion, resultado);
        
        return evento;
    }
    
   
}
