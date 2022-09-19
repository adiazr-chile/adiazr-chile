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
public class NotificacionSolicitudPermisoAdministrativoVO implements Serializable{

    private static final long serialVersionUID = 4477742295633779620L;

    private String tipoEvento;
    private String mensajeFinal;
    private String runTrabajador;
    private String nombreTrabajador;
    private String institucion;
    private String centroCosto;
    private String fechaHoraSolicitud;
    private String inicioPermisoAdministrativo;
    private String terminoPermisoAdministrativo;
    private double diasSolicitados;
    private String horaInicio;
    private String horaFin;
    private int semestre;

    public int getSemestre() {
        return semestre;
    }

    public void setSemestre(int semestre) {
        this.semestre = semestre;
    }
    
    
    
    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

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

    public String getInicioPermisoAdministrativo() {
        return inicioPermisoAdministrativo;
    }

    public void setInicioPermisoAdministrativo(String inicioPermisoAdministrativo) {
        this.inicioPermisoAdministrativo = inicioPermisoAdministrativo;
    }

    public String getTerminoPermisoAdministrativo() {
        return terminoPermisoAdministrativo;
    }

    public void setTerminoPermisoAdministrativo(String terminoPermisoAdministrativo) {
        this.terminoPermisoAdministrativo = terminoPermisoAdministrativo;
    }
    
}
