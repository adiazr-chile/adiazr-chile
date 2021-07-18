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
public class ProcesoEjecucionVO implements Serializable{

    private static final long serialVersionUID = 873463885698776220L;

    private String empresaId;
    private String deptoId;
    private int cencoId;
    private int procesoId;
    private String procesoName;
    private String fechaHoraInicioEjecucion;
    private String fechaHoraFinEjecucion;
    private String resultado;
    private String usuario;
    private String deptoNombre;
    private String cencoNombre;

    public String getDeptoNombre() {
        return deptoNombre;
    }

    public void setDeptoNombre(String deptoNombre) {
        this.deptoNombre = deptoNombre;
    }

    public String getCencoNombre() {
        return cencoNombre;
    }

    public void setCencoNombre(String cencoNombre) {
        this.cencoNombre = cencoNombre;
    }

    /**
     *
     * @return
     */
    public String getDeptoId() {
        return deptoId;
    }

    public void setDeptoId(String deptoId) {
        this.deptoId = deptoId;
    }

    public int getCencoId() {
        return cencoId;
    }

    public void setCencoId(int cencoId) {
        this.cencoId = cencoId;
    }

    
    
    public String getProcesoName() {
        return procesoName;
    }

    public void setProcesoName(String procesoName) {
        this.procesoName = procesoName;
    }

    public String getUsuario() {
        return usuario;
    }

    /**
     *
     * @param usuario
     */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    public int getProcesoId() {
        return procesoId;
    }

    /**
     *
     * @param procesoId
     */
    public void setProcesoId(int procesoId) {
        this.procesoId = procesoId;
    }

    public String getFechaHoraInicioEjecucion() {
        return fechaHoraInicioEjecucion;
    }

    public void setFechaHoraInicioEjecucion(String fechaHoraInicioEjecucion) {
        this.fechaHoraInicioEjecucion = fechaHoraInicioEjecucion;
    }

    public String getFechaHoraFinEjecucion() {
        return fechaHoraFinEjecucion;
    }

    public void setFechaHoraFinEjecucion(String fechaHoraFinEjecucion) {
        this.fechaHoraFinEjecucion = fechaHoraFinEjecucion;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
    
    
}
