/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.vo;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Alexander
 */
public class TiempoExtraVO implements Serializable{

    private static final long serialVersionUID = 7784412295698779620L;

    private String rut;
    private Date fecha;
    private String fechaAsStr;
    private String tiempoExtra;
    private String fechaIngresoAsStr;
    private String fechaActualizacionAsStr;
    private String usuarioResponsable;
    private String horas="00";
    private String minutos="00";
    private String tipo;
        
    public TiempoExtraVO() {
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    
    
    public String getHoras() {
        return horas;
    }

    public void setHoras(String horas) {
        this.horas = horas;
    }

    /**
     *
     * @return
     */
    public String getMinutos() {
        return minutos;
    }

    public void setMinutos(String minutos) {
        this.minutos = minutos;
    }

    /**
     *
     * @return
     */
    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getFechaAsStr() {
        return fechaAsStr;
    }

    public void setFechaAsStr(String fechaAsStr) {
        this.fechaAsStr = fechaAsStr;
    }

    public String getTiempoExtra() {
        return tiempoExtra;
    }

    public void setTiempoExtra(String tiempoExtra) {
        this.tiempoExtra = tiempoExtra;
    }

    public String getFechaIngresoAsStr() {
        return fechaIngresoAsStr;
    }

    public void setFechaIngresoAsStr(String fechaIngresoAsStr) {
        this.fechaIngresoAsStr = fechaIngresoAsStr;
    }

    /**
     *
     * @return
     */
    public String getFechaActualizacionAsStr() {
        return fechaActualizacionAsStr;
    }

    /**
     *
     * @param fechaActualizacionAsStr
     */
    public void setFechaActualizacionAsStr(String fechaActualizacionAsStr) {
        this.fechaActualizacionAsStr = fechaActualizacionAsStr;
    }

    public String getUsuarioResponsable() {
        return usuarioResponsable;
    }

    public void setUsuarioResponsable(String usuarioResponsable) {
        this.usuarioResponsable = usuarioResponsable;
    }

    
    
    
}
