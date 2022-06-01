/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.vo;

import java.io.Serializable;

/**
*
* @author Alexander
*/
public class MensajeUsuarioVO implements Serializable{
    private static final long serialVersionUID = 6579612338799776220L;
   
    String label;
    String value;
    String type="NORMAL";

    public MensajeUsuarioVO(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }
    
    public String getValue() {
        return value;
    }
    
}
