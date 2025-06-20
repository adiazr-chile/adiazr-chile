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
public class ModificacionTurnoVO implements Serializable{

    private static final long serialVersionUID = 7419472295698776220L;

    private String fechaAsignacionTurnoAnterior;
    private String nombreTurnoAnterior;
    private String quienAsignaTurnoAnterior;

    private String fechaAsignacionTurnoNuevo;
    private String nombreTurnoNuevo;
    private String quienAsignaTurnoNuevo;

    public String getFechaAsignacionTurnoAnterior() {
        return fechaAsignacionTurnoAnterior;
    }

    public void setFechaAsignacionTurnoAnterior(String fechaAsignacionTurnoAnterior) {
        this.fechaAsignacionTurnoAnterior = fechaAsignacionTurnoAnterior;
    }

    public String getNombreTurnoAnterior() {
        return nombreTurnoAnterior;
    }

    public void setNombreTurnoAnterior(String nombreTurnoAnterior) {
        this.nombreTurnoAnterior = nombreTurnoAnterior;
    }

    public String getQuienAsignaTurnoAnterior() {
        return quienAsignaTurnoAnterior;
    }

    public void setQuienAsignaTurnoAnterior(String quienAsignaTurnoAnterior) {
        this.quienAsignaTurnoAnterior = quienAsignaTurnoAnterior;
    }

    public String getFechaAsignacionTurnoNuevo() {
        return fechaAsignacionTurnoNuevo;
    }

    public void setFechaAsignacionTurnoNuevo(String fechaAsignacionTurnoNuevo) {
        this.fechaAsignacionTurnoNuevo = fechaAsignacionTurnoNuevo;
    }

    public String getNombreTurnoNuevo() {
        return nombreTurnoNuevo;
    }

    public void setNombreTurnoNuevo(String nombreTurnoNuevo) {
        this.nombreTurnoNuevo = nombreTurnoNuevo;
    }

    public String getQuienAsignaTurnoNuevo() {
        return quienAsignaTurnoNuevo;
    }

    public void setQuienAsignaTurnoNuevo(String quienAsignaTurnoNuevo) {
        this.quienAsignaTurnoNuevo = quienAsignaTurnoNuevo;
    }
    
    
    
    
}
