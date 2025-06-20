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
public class AlertaSistemaVO implements Serializable{

    private static final long serialVersionUID = 555774229569759620L;

    private int idAlerta;
    private String empresaId;
    private String titulo;
    private String mensaje;
    private String tipo; // Puede ser 'mantenimiento', 'aviso', 'importante'
    private String fechaEnvio; // Timestamp como String
    private String estado; // Puede ser 'pendiente', 'enviado', 'cancelado'
    private String prioridad; // Puede ser 'alta', 'media', 'baja'
    private String urlAccion;
    private String icono;
    private String creadoPor;
    private String modificadoPor;
    private String createdAt; // Timestamp como String
    private String updatedAt; // Timestamp como String
    private String fechaHoraDesde;
    private String fechaHoraHasta;
    
    // Constructor vacío

    public AlertaSistemaVO() {
    }

    public String getFechaHoraDesde() {
        return fechaHoraDesde;
    }

    public void setFechaHoraDesde(String fechaHoraDesde) {
        this.fechaHoraDesde = fechaHoraDesde;
    }

    public String getFechaHoraHasta() {
        return fechaHoraHasta;
    }

    public void setFechaHoraHasta(String fechaHoraHasta) {
        this.fechaHoraHasta = fechaHoraHasta;
    }

    public int getIdAlerta() {
        return idAlerta;
    }

    public void setIdAlerta(int idAlerta) {
        this.idAlerta = idAlerta;
    }

    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(String fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getUrlAccion() {
        return urlAccion;
    }

    public void setUrlAccion(String urlAccion) {
        this.urlAccion = urlAccion;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(String creadoPor) {
        this.creadoPor = creadoPor;
    }

    public String getModificadoPor() {
        return modificadoPor;
    }

    public void setModificadoPor(String modificadoPor) {
        this.modificadoPor = modificadoPor;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    

}
