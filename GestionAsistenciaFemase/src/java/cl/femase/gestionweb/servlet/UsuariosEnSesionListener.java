/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cl.femase.gestionweb.servlet;

import cl.femase.gestionweb.vo.UsuarioVO;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.util.concurrent.ConcurrentHashMap;

/**
*
* @author aledi
*/
@WebListener
public class UsuariosEnSesionListener implements HttpSessionAttributeListener, HttpSessionListener {
    private static final String ATRIBUTO_USUARIO = "usuarioObj";
    private static final String USUARIOS_ACTIVOS = "usuariosActivos";

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        if (ATRIBUTO_USUARIO.equals(event.getName())) {
            UsuarioVO usuario = (UsuarioVO) event.getValue();
            ServletContext ctx = event.getSession().getServletContext();
            ConcurrentHashMap<String, UsuarioVO> usuarios = 
                (ConcurrentHashMap<String, UsuarioVO>) ctx.getAttribute(USUARIOS_ACTIVOS);
            if (usuarios == null) {
                usuarios = new ConcurrentHashMap<>();
                ctx.setAttribute(USUARIOS_ACTIVOS, usuarios);
            }
            usuarios.put(event.getSession().getId(), usuario);
        }
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        if (ATRIBUTO_USUARIO.equals(event.getName())) {
            ServletContext ctx = event.getSession().getServletContext();
            ConcurrentHashMap<String, UsuarioVO> usuarios = 
                (ConcurrentHashMap<String, UsuarioVO>) ctx.getAttribute(USUARIOS_ACTIVOS);
            if (usuarios != null) {
                usuarios.remove(event.getSession().getId());
            }
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        ServletContext ctx = event.getSession().getServletContext();
        ConcurrentHashMap<String, UsuarioVO> usuarios = 
            (ConcurrentHashMap<String, UsuarioVO>) ctx.getAttribute(USUARIOS_ACTIVOS);
        if (usuarios != null) {
            usuarios.remove(event.getSession().getId());
        }
    }

    // Métodos attributeReplaced(...) y sessionCreated(...) si los necesitas
}
