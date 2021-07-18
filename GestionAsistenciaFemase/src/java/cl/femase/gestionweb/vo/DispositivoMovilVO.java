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
public class DispositivoMovilVO implements Serializable{

    private static final long serialVersionUID = 3259441285698776220L;
    
    private int correlativo;
    private String id;
    private String androidId;
    private String fechaHoraCreacion;
    private String directorRut;
    private String directorNombre;
    private int directorCencoId;
    private int estado;
    private String empresaId;
    private String cencoNombre;

    public String getDirectorNombre() {
        return directorNombre;
    }

    public void setDirectorNombre(String directorNombre) {
        this.directorNombre = directorNombre;
    }

    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    public String getCencoNombre() {
        return cencoNombre;
    }

    public void setCencoNombre(String cencoNombre) {
        this.cencoNombre = cencoNombre;
    }
    
    
    
    public int getCorrelativo() {
        return correlativo;
    }

    public void setCorrelativo(int correlativo) {
        this.correlativo = correlativo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public String getFechaHoraCreacion() {
        return fechaHoraCreacion;
    }

    public void setFechaHoraCreacion(String fechaHoraCreacion) {
        this.fechaHoraCreacion = fechaHoraCreacion;
    }

    public String getDirectorRut() {
        return directorRut;
    }

    public void setDirectorRut(String directorRut) {
        this.directorRut = directorRut;
    }

    public int getDirectorCencoId() {
        return directorCencoId;
    }

    public void setDirectorCencoId(int directorCencoId) {
        this.directorCencoId = directorCencoId;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    
    
}
