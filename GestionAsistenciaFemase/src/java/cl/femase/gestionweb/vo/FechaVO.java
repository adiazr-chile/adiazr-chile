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
public class FechaVO implements Serializable{

    private static final long serialVersionUID = 7599692295698776220L;

    private int anio;
    private int mes;
    private String anioMes;
    private String fecha;

    public FechaVO(int _anio, int _mes, String _fecha,String _anioMes) {
        this.anio = _anio;
        this.mes = _mes;
        this.fecha = _fecha;
        this.anioMes = _anioMes;
    }

    public String getAnioMes() {
        return anioMes;
    }

    public void setAnioMes(String anioMes) {
        this.anioMes = anioMes;
    }
    
    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public int getMes() {
        return mes;
    }

    /**
     *
     * @param mes
     */
    public void setMes(int mes) {
        this.mes = mes;
    }

    /**
     *
     * @return
     */
    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "FechaVO{" + "anio=" + anio + ", mes=" + mes + ", anioMes=" + anioMes + ", fecha=" + fecha + '}';
    }

}
