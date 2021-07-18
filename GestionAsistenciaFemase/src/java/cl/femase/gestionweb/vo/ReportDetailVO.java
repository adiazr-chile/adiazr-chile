/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.vo;

/**
 *
 * @author Alexander
 */
public class ReportDetailVO {
    private String fecha;
    private String labelTurno;
    private String horasTrabajadas  = "";
    private String horaEntrada      = "";
    private String horaSalida       = "";
    private String presencia        = "";
    private String horasExtras      = "";
    private String asistencia       = "";
    private String justificacion    = "";

    private String horario              = "";
    private String fechaAsignacionTurno = "";
    private String nuevoHorario         = "";
    private String descripcion          = "";
    private String observacion          = "";

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getFechaAsignacionTurno() {
        return fechaAsignacionTurno;
    }

    public void setFechaAsignacionTurno(String fechaAsignacionTurno) {
        this.fechaAsignacionTurno = fechaAsignacionTurno;
    }

    public String getNuevoHorario() {
        return nuevoHorario;
    }

    public void setNuevoHorario(String nuevoHorario) {
        this.nuevoHorario = nuevoHorario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    
    
    public String getAsistencia() {
        return asistencia;
    }

    public void setAsistencia(String asistencia) {
        this.asistencia = asistencia;
    }

    public String getJustificacion() {
        return justificacion;
    }

    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }

    public String getPresencia() {
        return presencia;
    }

    public void setPresencia(String presencia) {
        this.presencia = presencia;
    }

    public String getHorasExtras() {
        return horasExtras;
    }

    public void setHorasExtras(String horasExtras) {
        this.horasExtras = horasExtras;
    }

    
    
    public String getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(String horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public String getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(String horaSalida) {
        this.horaSalida = horaSalida;
    }
    
    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getLabelTurno() {
        return labelTurno;
    }

    public void setLabelTurno(String labelTurno) {
        this.labelTurno = labelTurno;
    }

    public String getHorasTrabajadas() {
        return horasTrabajadas;
    }

    public void setHorasTrabajadas(String horasTrabajadas) {
        this.horasTrabajadas = horasTrabajadas;
    }
    
    
    
}
