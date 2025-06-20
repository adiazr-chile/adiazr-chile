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
public class ModificacionAlteracionTurnoVO implements Serializable{

    private static final long serialVersionUID = 7591252295698776220L;

    int idTurnoAnterior;  
    String fechaAsignacionTurnoAnterior;	
    String nombreTurnoAnterior;
    String quienAsignaTurnoAnterior;
    
    int idTurnoNuevo;  
    String fechaAsignacionTurnoNuevo;	
    String nombreTurnoNuevo;
    String quienAsignaTurnoNuevo;

    public int getIdTurnoNuevo() {
        return idTurnoNuevo;
    }

    public void setIdTurnoNuevo(int idTurnoNuevo) {
        this.idTurnoNuevo = idTurnoNuevo;
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

    
    
    public int getIdTurnoAnterior() {
        return idTurnoAnterior;
    }

    public void setIdTurnoAnterior(int idTurnoAnterior) {
        this.idTurnoAnterior = idTurnoAnterior;
    }

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

    @Override
    public String toString() {
        return "ModificacionAlteracionTurnoVO{" + "idTurnoAnterior=" + idTurnoAnterior + ", fechaAsignacionTurnoAnterior=" + fechaAsignacionTurnoAnterior + ", nombreTurnoAnterior=" + nombreTurnoAnterior + ", quienAsignaTurnoAnterior=" + quienAsignaTurnoAnterior + ", idTurnoNuevo=" + idTurnoNuevo + ", fechaAsignacionTurnoNuevo=" + fechaAsignacionTurnoNuevo + ", nombreTurnoNuevo=" + nombreTurnoNuevo + ", quienAsignaTurnoNuevo=" + quienAsignaTurnoNuevo + '}';
    }
    
    
    
    
}
