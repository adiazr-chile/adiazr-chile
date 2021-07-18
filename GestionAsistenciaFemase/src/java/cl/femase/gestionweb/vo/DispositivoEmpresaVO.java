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
public class DispositivoEmpresaVO implements Serializable{

    private static final long serialVersionUID = 7598771285698776220L;

    private final String deviceId;
    private final String empresaId;
    private String empresaNombre;

    public String getDeviceId() {
        return deviceId;
    }

    public String getEmpresaId() {
        return empresaId;
    }

    public DispositivoEmpresaVO(String deviceId, String empresaId) {
        this.deviceId = deviceId;
        this.empresaId = empresaId;
    }

    public String getEmpresaNombre() {
        return empresaNombre;
    }

    public void setEmpresaNombre(String empresaNombre) {
        this.empresaNombre = empresaNombre;
    }
    
    
    
    
}
