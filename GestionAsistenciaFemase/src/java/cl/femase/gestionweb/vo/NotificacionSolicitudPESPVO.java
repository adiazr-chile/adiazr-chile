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
public class NotificacionSolicitudPESPVO implements Serializable{

    private static final long serialVersionUID = 4847742295633779620L;

    private String tipoEvento;
    private String mensajeFinal;
    private String runTrabajador;
    private String nombreTrabajador;
    private String institucion;
    private String centroCosto;
    private String fechaHoraSolicitud;
    private String inicioPermiso;
    private String terminoPermiso;
    private double diasSolicitados;
    
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

    public double getDiasSolicitados() {
        return diasSolicitados;
    }

    public void setDiasSolicitados(double diasSolicitados) {
        this.diasSolicitados = diasSolicitados;
    }

    public String getInicioPermiso() {
        return inicioPermiso;
    }

    public void setInicioPermiso(String inicioPermiso) {
        this.inicioPermiso = inicioPermiso;
    }

    public String getTerminoPermiso() {
        return terminoPermiso;
    }

    public void setTerminoPermiso(String terminoPermiso) {
        this.terminoPermiso = terminoPermiso;
    }

    
    
}
