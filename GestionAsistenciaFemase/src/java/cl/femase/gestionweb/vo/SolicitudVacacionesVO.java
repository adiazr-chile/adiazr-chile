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
public class SolicitudVacacionesVO implements Serializable{

    private static final long serialVersionUID = 517992295698776220L;

    private int id;
    private String fechaIngreso;
    private String estadoId;
    private String estadoLabel;
    private String inicioVacaciones;
    private String finVacaciones;
    private String empresaId;
    private String rutEmpleado;
    private String nombreEmpleado;
    private String usernameSolicita;
    private String usernameApruebaRechaza;
    private String fechaHoraCancela;
    private String fechaHoraApruebaRechaza;
    private String notaObservacion;
    private String labelEmpleado;
    
    private int diasEfectivosVacacionesSolicitadas;
    private double saldoVacaciones;
    private String diasEspeciales;

    public String getLabelEmpleado() {
        return labelEmpleado;
    }

    public void setLabelEmpleado(String labelEmpleado) {
        this.labelEmpleado = labelEmpleado;
    }

    public int getDiasEfectivosVacacionesSolicitadas() {
        return diasEfectivosVacacionesSolicitadas;
    }

    public void setDiasEfectivosVacacionesSolicitadas(int diasEfectivosVacacionesSolicitadas) {
        this.diasEfectivosVacacionesSolicitadas = diasEfectivosVacacionesSolicitadas;
    }

    public double getSaldoVacaciones() {
        return saldoVacaciones;
    }

    public void setSaldoVacaciones(double saldoVacaciones) {
        this.saldoVacaciones = saldoVacaciones;
    }

    public String getDiasEspeciales() {
        return diasEspeciales;
    }

    public void setDiasEspeciales(String diasEspeciales) {
        this.diasEspeciales = diasEspeciales;
    }
    
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

    public String getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(String estadoId) {
        this.estadoId = estadoId;
    }

    public String getEstadoLabel() {
        return estadoLabel;
    }

    public void setEstadoLabel(String estadoLabel) {
        this.estadoLabel = estadoLabel;
    }

    public String getInicioVacaciones() {
        return inicioVacaciones;
    }

    public void setInicioVacaciones(String inicioVacaciones) {
        this.inicioVacaciones = inicioVacaciones;
    }

    public String getFinVacaciones() {
        return finVacaciones;
    }

    public void setFinVacaciones(String finVacaciones) {
        this.finVacaciones = finVacaciones;
    }

    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    public String getRutEmpleado() {
        return rutEmpleado;
    }

    public void setRutEmpleado(String rutEmpleado) {
        this.rutEmpleado = rutEmpleado;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
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

    public String getFechaHoraCancela() {
        return fechaHoraCancela;
    }

    public void setFechaHoraCancela(String fechaHoraCancela) {
        this.fechaHoraCancela = fechaHoraCancela;
    }

    public String getFechaHoraApruebaRechaza() {
        return fechaHoraApruebaRechaza;
    }

    public void setFechaHoraApruebaRechaza(String fechaHoraApruebaRechaza) {
        this.fechaHoraApruebaRechaza = fechaHoraApruebaRechaza;
    }

    public String getNotaObservacion() {
        return notaObservacion;
    }

    public void setNotaObservacion(String notaObservacion) {
        this.notaObservacion = notaObservacion;
    }

    
}
