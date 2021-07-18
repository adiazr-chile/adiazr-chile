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
public class DispositivoDepartamentoVO implements Serializable{

    private static final long serialVersionUID = 7598773695698776220L;

    private final String deviceId;
    private final String deptoId;
    private String deptoNombre;

    /**
     *
     * @return
     */
    public String getDeptoNombre() {
        return deptoNombre;
    }

    public void setDeptoNombre(String deptoNombre) {
        this.deptoNombre = deptoNombre;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeptoId() {
        return deptoId;
    }

    public DispositivoDepartamentoVO(String deviceId, String deptoId) {
        this.deviceId = deviceId;
        this.deptoId = deptoId;
    }
    
    
    
}
