/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cl.femase.gestionweb.servlet;

import cl.femase.gestionweb.business.NotificacionBp;
import cl.femase.gestionweb.business.UsuarioBp;
import cl.femase.gestionweb.common.Constantes;
import cl.femase.gestionweb.common.PasswordGenerator;
import cl.femase.gestionweb.common.Utilidades;
import cl.femase.gestionweb.vo.MaintenanceEventVO;
import cl.femase.gestionweb.vo.NotificacionSolicitudClaveVO;
import cl.femase.gestionweb.vo.NotificacionVO;
import cl.femase.gestionweb.vo.PropertiesVO;
import cl.femase.gestionweb.vo.ResultCRUDVO;
import cl.femase.gestionweb.vo.UsuarioVO;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class RequestAccessCodeServlet extends BaseServlet {
    
    @Override
    protected void doPost(HttpServletRequest _request, HttpServletResponse _response) throws ServletException, IOException {
        
        HttpSession session         = _request.getSession(true);
        ServletContext application  = this.getServletContext();
        PropertiesVO appProperties  = (PropertiesVO)application.getAttribute("appProperties");
//        UsuarioVO userConnected     = (UsuarioVO)session.getAttribute("usuarioObj");
        String email                = _request.getParameter("emailInput");
        String empresaId            = "emp01";
        String newEmail             = email + "@" + appProperties.getDTmailServerDomain();
        String newUsername          = Utilidades.getUsername(email);
                
        System.out.println(WEB_NAME+"[RequestAccessCodeServlet]"
            + "emailInput(prefijo): " + email
            + ", email: " + newEmail
            + ", newUsername: " + newUsername);
        
        /**
        Dado que la columna usuario.usr_username tiene largo 12:
            Para obtener el nombre de usuario se debe truncar a 9 el prefijo del correo antes del  “@‌dt.gob.cl”. Por ejemplo, si el correo es ‘ictsantiagoponiente@dt.gob.cl’, el nombre de usuario será: ictsantia + número_random de 3 dígitos.---> 'ictsantia505’. 
            * Este nombre de usuario debe enviarse por correo junto a la clave asignada
        El sistema reconocerá el dominio @‌dt.gob.cl y:
        .- Si el correo no existe: creará el nuevo usuario con perfil 'Fiscalizador'
        .- Si el correo existe, se debe Activar el usuario (dejarlo vigente)
        El Sistema enviará por correo automáticamente: el nombre de usuario asignado junto a la clave de acceso a dicha cuenta de correo (que cumpla los requisitos mínimos de seguridad)
        Por razones de seguridad, la contraseña tendrá una duración de 5 días corridos, transcurridos los cuales caducará y el usuario quedará como No Vigente.
        */
        
        UsuarioBp usersBp = new UsuarioBp(appProperties);
        boolean existeCorreoUsuario = usersBp.existeEmail(newEmail);
        UsuarioVO existeUsername    = usersBp.getUsuario(newUsername);
        
        if (existeUsername == null && !existeCorreoUsuario){
            //crear password del usuario
            String password = PasswordGenerator.generateRandomPassword(12);
            
            System.out.println(WEB_NAME+"[RequestAccessCodeServlet]"
                + "Insertar registro en tabla 'notificacion'");
            //insertar registro en la tabla notificacion para enviar usuario y clave        
            notificaSolicitudClave(appProperties, 
                empresaId, 
                "Solicitud clave temporal usuario DT: " + newUsername, 
                _request, 
                newUsername, 
                password, 
                newEmail);
            //crear usuario pero este debe quedar NO Vigente 5 dias despues
            //crear el usuario...con  atributo usuario.temporal_user='S'
                    
            MaintenanceEventVO resultado=new MaintenanceEventVO();
            resultado.setUsername(newUsername);
            resultado.setDatetime(new Date());
            resultado.setUserIP(_request.getRemoteAddr());
            resultado.setType("USR");
            resultado.setEmpresaIdSource(empresaId);
                        
            //objeto usado para insert
            UsuarioVO userdata = new UsuarioVO();
            userdata.setUsername(newUsername);
            userdata.setPassword(password);
            userdata.setEstado(Constantes.ESTADO_VIGENTE);
            userdata.setIdPerfil(Constantes.ID_PERFIL_FISCALIZADOR);
            userdata.setEmail(newEmail);
            userdata.setEmpresaId(empresaId);
            userdata.setIsTemporary(true);
            userdata.setNombres(empresaId);
             
            ResultCRUDVO result = usersBp.insertTemporaryUser(userdata, resultado);
            if (result.isThereError()){
                System.out.println(WEB_NAME+"[RequestAccessCodeServlet]"
                    + "Error al crear usuario temporal: "+result.getMsg());
                _request.setAttribute("mensaje", result.getMsg());
                //_request.getRequestDispatcher("/mantencion/usuario_form.jsp").forward(_request, _response);//frameset
            }
                                        
            _request.getRequestDispatcher("/index.jsp").forward(_request, _response);
                
        }
        
                
    }
    
    /**
    * 
    * 
    */
    private NotificacionSolicitudClaveVO notificaSolicitudClave(
            PropertiesVO _appProperties,
            String _empresaId,
            String _evento,
            HttpServletRequest _request,
            String _username,
            String _claveTemporal,
            String _emailUsuario){
    
        NotificacionSolicitudClaveVO evento = new NotificacionSolicitudClaveVO();
                
        Calendar cal            = Calendar.getInstance(new Locale("es","CL"));
        Date fechaActual        = cal.getTime();    
        SimpleDateFormat sdf    = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("es","CL"));
        String fromLabel        = "Gestion asistencia";
        String fromMail         = _appProperties.getMailFrom();
        String asuntoMail       = "Sistema de Gestion-Solicitud de clave temporal";
        String mailTo           = _emailUsuario;
        SimpleDateFormat sdf2    = new SimpleDateFormat("yyyy-MM-dd");
                
        String mensaje = "Se ha generado una contraseña temporal "
            + "para que pueda acceder al Sistema. "
            + "Por favor, utilice las siguientes credenciales para iniciar sesión: "
            + "Usuario: [" + _username + "] "
                + "Contraseña temporal: [" + _claveTemporal + "]"
                + ""
                + "Por motivos de seguridad, le recomendamos cambiar "
                + "su contraseña inmediatamente después de iniciar sesión."
                + " "
                + "Puede acceder al sistema haciendo clic en el siguiente enlace: "
                + "/newLogin/index.jsp "
                + " "
                + "Si tiene algún problema para acceder o necesita "
                + "asistencia, no dude en ponerse en contacto con nuestro equipo de soporte. "
                + " "
                + "Atentamente, Soporte FEMASE";
        
        String mailBody = "<html>" +
            "<body>" +
            "<p>Estimado/a <b>[" + _emailUsuario + "]</b>,</p>" +
            "<p>Se ha generado una <b>contraseña temporal</b> para que pueda acceder al sistema. "
            + "Por favor, utilice las siguientes credenciales para iniciar sesión:</p>" +
            "<p><b>Usuario:</b> " + _username +  "<br>" +
            "<b>Contraseña temporal:</b> "+ _claveTemporal + 
            "<p>Puede acceder al sistema haciendo clic en el siguiente enlace:<br>" +
            "<a href=\"https://gestion.femase.cl/\">Ingresar al Sistema</a></p>" +
            "<p>Si tiene algún problema para acceder o necesita asistencia, no dude en ponerse en contacto con nuestro equipo de soporte.</p>" +
            "<p>Atentamente,<br>" +
            "FEMASE<br>" +
            "</body>" +
            "</html>";
        
        evento.setTipoEvento("Solicita clave");
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
