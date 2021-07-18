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
public class DetalleAsistenciaToInsertVO implements Serializable{

    private static final long serialVersionUID = 7457742295698776220L;

    private final DetalleAsistenciaVO _detalle;

    public DetalleAsistenciaToInsertVO(DetalleAsistenciaVO _detalle) {
        this._detalle = _detalle;
    }

    public DetalleAsistenciaVO getDetalle() {
        return _detalle;
    }
   
}
