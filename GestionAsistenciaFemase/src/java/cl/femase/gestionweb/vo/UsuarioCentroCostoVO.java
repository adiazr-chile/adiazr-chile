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
public class UsuarioCentroCostoVO implements Serializable {

    private static final long serialVersionUID = 7598772295622776220L;
    private String username;
    private int ccostoId;
    private String ccostoNombre;
    private int porDefecto=0;

    private String empresaId;
    private String empresaNombre;
    private String deptoId;
    private String deptoNombre;
    private String zonaExtrema;
    
    public UsuarioCentroCostoVO(String _username, int _cencoId, 
            int _porDefecto,String _ccostoNombre,
            String _empresaId,String _deptoId) {
        this.username = _username;
        this.ccostoId = _cencoId;
        this.porDefecto = _porDefecto;
        this.ccostoNombre = _ccostoNombre;
        this.empresaId = _empresaId;
        this.deptoId = _deptoId;
    }

    public String getZonaExtrema() {
        return zonaExtrema;
    }

    public void setZonaExtrema(String zonaExtrema) {
        this.zonaExtrema = zonaExtrema;
    }

    
    
    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    public String getEmpresaNombre() {
        return empresaNombre;
    }

    public void setEmpresaNombre(String empresaNombre) {
        this.empresaNombre = empresaNombre;
    }

    public String getDeptoId() {
        return deptoId;
    }

    public void setDeptoId(String deptoId) {
        this.deptoId = deptoId;
    }

    public String getDeptoNombre() {
        return deptoNombre;
    }

    public void setDeptoNombre(String deptoNombre) {
        this.deptoNombre = deptoNombre;
    }

    
    
    public String getCcostoNombre() {
        return ccostoNombre;
    }

    public void setCcostoNombre(String ccostoNombre) {
        this.ccostoNombre = ccostoNombre;
    }

    
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getCcostoId() {
        return ccostoId;
    }

    /**
     *
     * @param ccostoId
     */
    public void setCcostoId(int ccostoId) {
        this.ccostoId = ccostoId;
    }

    public int getPorDefecto() {
        return porDefecto;
    }

    public void setPorDefecto(int porDefecto) {
        this.porDefecto = porDefecto;
    }
    
    
}
