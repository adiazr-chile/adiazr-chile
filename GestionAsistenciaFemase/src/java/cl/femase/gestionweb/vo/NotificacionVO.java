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
public class NotificacionVO implements Serializable{

    private static final long serialVersionUID = 6637787995698779620L;

    private int correlativo;
    private String fechaIngreso;
    private String fechaEnvioEmail;
    private boolean correoEnviado;
    private String mailFrom;
    private String mailTo;
    private String mailSubject;
    private String mailBody;
    private String empresaId;
    private int cencoId;
    private String rutEmpleado;
    private String username;
    
    private String marcaVirtual = "N";
    private String latitud;
    private String longitud;
    private String registraUbicacion = "N";
    private String comentario;
    
    public NotificacionVO() {
        
    }

    public String getMarcaVirtual() {
        return marcaVirtual;
    }

    public void setMarcaVirtual(String marcaVirtual) {
        this.marcaVirtual = marcaVirtual;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getRegistraUbicacion() {
        return registraUbicacion;
    }

    public void setRegistraUbicacion(String registraUbicacion) {
        this.registraUbicacion = registraUbicacion;
    }
    
    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    public int getCencoId() {
        return cencoId;
    }

    public void setCencoId(int cencoId) {
        this.cencoId = cencoId;
    }

    public String getRutEmpleado() {
        return rutEmpleado;
    }

    public void setRutEmpleado(String rutEmpleado) {
        this.rutEmpleado = rutEmpleado;
    }

    
    
    public int getCorrelativo() {
        return correlativo;
    }

    public void setCorrelativo(int correlativo) {
        this.correlativo = correlativo;
    }

    public String getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(String fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getFechaEnvioEmail() {
        return fechaEnvioEmail;
    }

    public void setFechaEnvioEmail(String fechaEnvioEmail) {
        this.fechaEnvioEmail = fechaEnvioEmail;
    }

    public boolean isCorreoEnviado() {
        return correoEnviado;
    }

    public void setCorreoEnviado(boolean correoEnviado) {
        this.correoEnviado = correoEnviado;
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public String getMailTo() {
        return mailTo;
    }

    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }

    public String getMailSubject() {
        return mailSubject;
    }

    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }

    public String getMailBody() {
        return mailBody;
    }

    public void setMailBody(String mailBody) {
        this.mailBody = mailBody;
    }

    
}
