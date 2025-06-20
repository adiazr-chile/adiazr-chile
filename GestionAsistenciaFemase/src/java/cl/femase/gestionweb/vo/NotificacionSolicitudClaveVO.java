/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.vo;

import java.io.Serializable;

/**
 *
 * @author Alexander
 */
public class NotificacionSolicitudClaveVO implements Serializable{

    private static final long serialVersionUID = 6981232295633779620L;

    private String tipoEvento;
    private String mensajeFinal;
    private String emailUsuario;
    private String username;
    private String password;

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public String getMensajeFinal() {
        return mensajeFinal;
    }

    public void setMensajeFinal(String mensajeFinal) {
        this.mensajeFinal = mensajeFinal;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    
    
}
