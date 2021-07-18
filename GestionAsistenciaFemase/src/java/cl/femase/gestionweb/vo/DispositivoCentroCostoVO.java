/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.vo;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 *
 * @author Alexander
 */
public class DispositivoCentroCostoVO implements Serializable{

    private static final long serialVersionUID = 7598773695879776220L;

    private final String deviceId;
    private final int cencoId;
    private String cencoNombre;
    
    public String getDeviceId() {
        return deviceId;
    }

    public int getCencoId() {
        return cencoId;
    }

    public DispositivoCentroCostoVO(String deviceId, int cencoId) {
        this.deviceId = deviceId;
        this.cencoId = cencoId;
    }

    public String getCencoNombre() {
        return cencoNombre;
    }

    public void setCencoNombre(String cencoNombre) {
        this.cencoNombre = cencoNombre;
    }
    
    
    
}
