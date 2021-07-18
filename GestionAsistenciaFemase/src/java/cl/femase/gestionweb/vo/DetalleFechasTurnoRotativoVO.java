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
public class DetalleFechasTurnoRotativoVO implements Serializable{

    private static final long serialVersionUID = 7599325295698776220L;

    private FechaVO fechaLaboral;
    private FechaVO fechaLibre;

    public FechaVO getFechaLaboral() {
        return fechaLaboral;
    }

    public void setFechaLaboral(FechaVO fechaLaboral) {
        this.fechaLaboral = fechaLaboral;
    }

    public FechaVO getFechaLibre() {
        return fechaLibre;
    }

    public void setFechaLibre(FechaVO fechaLibre) {
        this.fechaLibre = fechaLibre;
    }
    
    
}
