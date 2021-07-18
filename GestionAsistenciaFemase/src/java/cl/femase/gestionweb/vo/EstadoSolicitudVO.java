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
public class EstadoSolicitudVO implements Serializable{

    private static final long serialVersionUID = 987992295698776220L;

    private final String id;
    private final String label;
    private final String abrevacion;

    public EstadoSolicitudVO(String _id, String _label, String _abrevacion) {
        this.id         = _id;
        this.label      = _label;
        this.abrevacion = _abrevacion;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getAbrevacion() {
        return abrevacion;
    }

    
    
}
