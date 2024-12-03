/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cl.femase.gestionweb.vo;

import java.io.Serializable;
import java.time.LocalDate;

/**
 *
 * @author aledi
 */
public class VacacionesSaldoPeriodoVO implements Serializable{
    
    private static final long serialVersionUID = 987742293577459620L;
    
    private final int numeroPeriodo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    
    private String empresaId;
    private String runEmpleado;
    private String nombreEmpleado;
    private String fechaInicioPeriodo;
    private String fechaFinPeriodo;
    private double saldoVBA;
    private int estadoId;
    private String estadoNombre;
    private String updateDatetime;
    private String deptoNombre;
    private String cencoNombre;
    
    /**
    * 
    * @param numeroPeriodo
    * @param fechaInicio
    * @param fechaFin
    */
    public VacacionesSaldoPeriodoVO(int numeroPeriodo, LocalDate fechaInicio, LocalDate fechaFin) {
        this.numeroPeriodo = numeroPeriodo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    public String getDeptoNombre() {
        return deptoNombre;
    }

    public void setDeptoNombre(String deptoNombre) {
        this.deptoNombre = deptoNombre;
    }

    public String getCencoNombre() {
        return cencoNombre;
    }

    public void setCencoNombre(String cencoNombre) {
        this.cencoNombre = cencoNombre;
    }

    
    
    /**
    * 
    * @param _currentYear
    * @return 
    **/
    public boolean esVigente(int _currentYear) {
        return fechaInicio.getYear() >= _currentYear - 2 && fechaInicio.getYear() <= _currentYear;
    }
    
    public int getNumeroPeriodo() {
        return numeroPeriodo;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
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

    public String getFechaInicioPeriodo() {
        return fechaInicioPeriodo;
    }

    public void setFechaInicioPeriodo(String fechaInicioPeriodo) {
        this.fechaInicioPeriodo = fechaInicioPeriodo;
    }

    public String getFechaFinPeriodo() {
        return fechaFinPeriodo;
    }

    public void setFechaFinPeriodo(String fechaFinPeriodo) {
        this.fechaFinPeriodo = fechaFinPeriodo;
    }

    public double getSaldoVBA() {
        return saldoVBA;
    }

    public void setSaldoVBA(double saldoVBA) {
        this.saldoVBA = saldoVBA;
    }

    public int getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(int estadoId) {
        this.estadoId = estadoId;
    }

    public String getEstadoNombre() {
        return estadoNombre;
    }

    public void setEstadoNombre(String estadoNombre) {
        this.estadoNombre = estadoNombre;
    }

    public String getUpdateDatetime() {
        return updateDatetime;
    }

    public void setUpdateDatetime(String updateDatetime) {
        this.updateDatetime = updateDatetime;
    }
    
    
}
