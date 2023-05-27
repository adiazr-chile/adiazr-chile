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
public class SolicitudPermisoExamenSaludPreventivaVO implements Serializable{

    private static final long serialVersionUID = 517992295698744720L;

    private int id;
    private String fechaIngreso;
    private String empresaId;
    private String runEmpleado;
    private int anio;
    private String estadoId;
    private String fechaInicioPESP;
    private String fechaFinPESP;
    private int diasSolicitados;
    private String usernameSolicita;
    private String usernameApruebaRechaza;
    private String fechaHoraApruebaRechaza;
    private String fechaHoraCancela;
    private String notaObservacion;
    
    //adicionales
    private String estadoLabel;
    private String nombreEmpleado;
    private String labelEmpleado;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(String fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    public String getRunEmpleado() {
        return runEmpleado;
    }

    public void setRunEmpleado(String runEmpleado) {
        this.runEmpleado = runEmpleado;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(String estadoId) {
        this.estadoId = estadoId;
    }

    public String getFechaInicioPESP() {
        return fechaInicioPESP;
    }

    public void setFechaInicioPESP(String fechaInicioPESP) {
        this.fechaInicioPESP = fechaInicioPESP;
    }

    public String getFechaFinPESP() {
        return fechaFinPESP;
    }

    public void setFechaFinPESP(String fechaFinPESP) {
        this.fechaFinPESP = fechaFinPESP;
    }

    public int getDiasSolicitados() {
        return diasSolicitados;
    }

    public void setDiasSolicitados(int diasSolicitados) {
        this.diasSolicitados = diasSolicitados;
    }

    public String getUsernameSolicita() {
        return usernameSolicita;
    }

    public void setUsernameSolicita(String usernameSolicita) {
        this.usernameSolicita = usernameSolicita;
    }

    public String getUsernameApruebaRechaza() {
        return usernameApruebaRechaza;
    }

    public void setUsernameApruebaRechaza(String usernameApruebaRechaza) {
        this.usernameApruebaRechaza = usernameApruebaRechaza;
    }

    public String getFechaHoraApruebaRechaza() {
        return fechaHoraApruebaRechaza;
    }

    public void setFechaHoraApruebaRechaza(String fechaHoraApruebaRechaza) {
        this.fechaHoraApruebaRechaza = fechaHoraApruebaRechaza;
    }

    public String getFechaHoraCancela() {
        return fechaHoraCancela;
    }

    public void setFechaHoraCancela(String fechaHoraCancela) {
        this.fechaHoraCancela = fechaHoraCancela;
    }

    public String getNotaObservacion() {
        return notaObservacion;
    }

    public void setNotaObservacion(String notaObservacion) {
        this.notaObservacion = notaObservacion;
    }

    public String getEstadoLabel() {
        return estadoLabel;
    }

    public void setEstadoLabel(String estadoLabel) {
        this.estadoLabel = estadoLabel;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    public String getLabelEmpleado() {
        return labelEmpleado;
    }

    public void setLabelEmpleado(String labelEmpleado) {
        this.labelEmpleado = labelEmpleado;
    }
    
    
    
}
