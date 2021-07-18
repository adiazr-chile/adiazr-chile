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
public class TurnoRotativoDetalleVO extends TurnoRotativoVO implements Serializable{

    private static final long serialVersionUID = 3377742295698989620L;
   
    private String rutEmpleado;
    private int anio;
    private int mes;
    private String diasLaborales;
    private String diasLibres;
    private String fechaActualizacion;
    
    public TurnoRotativoDetalleVO() {
        
    }

    public String getRutEmpleado() {
        return rutEmpleado;
    }

    public void setRutEmpleado(String rutEmpleado) {
        this.rutEmpleado = rutEmpleado;
    }

    /**
     *
     * @return
     */
    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public String getDiasLaborales() {
        return diasLaborales;
    }

    public void setDiasLaborales(String diasLaborales) {
        this.diasLaborales = diasLaborales;
    }

    public String getDiasLibres() {
        return diasLibres;
    }

    public void setDiasLibres(String diasLibres) {
        this.diasLibres = diasLibres;
    }

    public String getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(String fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    
    
}
