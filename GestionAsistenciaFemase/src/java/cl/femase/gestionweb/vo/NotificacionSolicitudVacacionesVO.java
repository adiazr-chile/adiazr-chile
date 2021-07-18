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
public class NotificacionSolicitudVacacionesVO implements Serializable{

    private static final long serialVersionUID = 6987742295633779620L;

    private String tipoEvento;
    private String mensajeFinal;
    private String runTrabajador;
    private String nombreTrabajador;
    private String institucion;
    private String centroCosto;
    private String fechaHoraSolicitud;
    private String inicioVacaciones;
    private String terminoVacaciones;
    private int diasSolicitados;

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public String getMensajeFinal() {
        return mensajeFinal;
    }

    public void setMensajeFinal(String mensajeFinal) {
        this.mensajeFinal = mensajeFinal;
    }

    public String getRunTrabajador() {
        return runTrabajador;
    }

    public void setRunTrabajador(String runTrabajador) {
        this.runTrabajador = runTrabajador;
    }

    public String getNombreTrabajador() {
        return nombreTrabajador;
    }

    public void setNombreTrabajador(String nombreTrabajador) {
        this.nombreTrabajador = nombreTrabajador;
    }

    public String getInstitucion() {
        return institucion;
    }

    public void setInstitucion(String institucion) {
        this.institucion = institucion;
    }

    public String getCentroCosto() {
        return centroCosto;
    }

    public void setCentroCosto(String centroCosto) {
        this.centroCosto = centroCosto;
    }

    public String getFechaHoraSolicitud() {
        return fechaHoraSolicitud;
    }

    public void setFechaHoraSolicitud(String fechaHoraSolicitud) {
        this.fechaHoraSolicitud = fechaHoraSolicitud;
    }

    public String getInicioVacaciones() {
        return inicioVacaciones;
    }

    public void setInicioVacaciones(String inicioVacaciones) {
        this.inicioVacaciones = inicioVacaciones;
    }

    public String getTerminoVacaciones() {
        return terminoVacaciones;
    }

    public void setTerminoVacaciones(String terminoVacaciones) {
        this.terminoVacaciones = terminoVacaciones;
    }

    public int getDiasSolicitados() {
        return diasSolicitados;
    }

    public void setDiasSolicitados(int diasSolicitados) {
        this.diasSolicitados = diasSolicitados;
    }
    
    
    
}
