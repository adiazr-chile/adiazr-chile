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
public class MarcaRechazoVO extends MarcaVO implements Serializable{
    private static final long serialVersionUID = 7899472398799776220L;
   
    private String motivoRechazo;
    private String updateDateTime;

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    @Override
    public String getFechaHoraActualizacion() {
        return updateDateTime;
    }

    @Override
    public void setFechaHoraActualizacion(String fechaHoraActualizacion) {
        this.updateDateTime = fechaHoraActualizacion;
    }
    
}
