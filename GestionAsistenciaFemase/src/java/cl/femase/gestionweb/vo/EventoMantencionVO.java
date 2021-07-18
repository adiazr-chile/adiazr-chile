/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.vo;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Alexander
 */
public class EventoMantencionVO implements Serializable{

    private static final long serialVersionUID = 7989876985698776220L;

    private String username;
    private String descripcion;
    private String ip;
    private String tipoEventoId;
    private String tipoEventoNombre;
    private Date fechaHora;
    private String fechaHoraAsStr;
    private String empresaId;
    private String empresaNombre;
    private String deptoId;
    private String deptoNombre;
    private int cencoId;
    private String cencoNombre;
    private String rutEmpleado;

    private String operatingSystem;
    private String browserName;

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getBrowserName() {
        return browserName;
    }

    public void setBrowserName(String browserName) {
        this.browserName = browserName;
    }
    
    public String getEmpresaNombre() {
        return empresaNombre;
    }

    /**
     *
     * @param empresaNombre
     */
    public void setEmpresaNombre(String empresaNombre) {
        this.empresaNombre = empresaNombre;
    }

    public String getDeptoNombre() {
        return deptoNombre;
    }

    public void setDeptoNombre(String deptoNombre) {
        this.deptoNombre = deptoNombre;
    }

    /**
     *
     * @return
     */
    public String getCencoNombre() {
        return cencoNombre;
    }

    public void setCencoNombre(String cencoNombre) {
        this.cencoNombre = cencoNombre;
    }

    
    
    
    public String getRutEmpleado() {
        return rutEmpleado;
    }

    public void setRutEmpleado(String rutEmpleado) {
        this.rutEmpleado = rutEmpleado;
    }

    
    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    public String getDeptoId() {
        return deptoId;
    }

    public void setDeptoId(String deptoId) {
        this.deptoId = deptoId;
    }

    /**
     *
     * @return
     */
    public int getCencoId() {
        return cencoId;
    }

    public void setCencoId(int cencoId) {
        this.cencoId = cencoId;
    }

  
    
    /**
     * @return 
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIp() {
        return ip;
    }

    /**
     *
     * @param ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getTipoEventoId() {
        return tipoEventoId;
    }

    public void setTipoEventoId(String tipoEventoId) {
        this.tipoEventoId = tipoEventoId;
    }

    public String getTipoEventoNombre() {
        return tipoEventoNombre;
    }

    public void setTipoEventoNombre(String tipoEventoNombre) {
        this.tipoEventoNombre = tipoEventoNombre;
    }

    public Date getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Date fechaHora) {
        this.fechaHora = fechaHora;
    }

    /**
     *
     * @return
     */
    public String getFechaHoraAsStr() {
        return fechaHoraAsStr;
    }

    public void setFechaHoraAsStr(String fechaHoraAsStr) {
        this.fechaHoraAsStr = fechaHoraAsStr;
    }
    
    
}
