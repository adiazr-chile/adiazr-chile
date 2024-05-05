/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.vo;

import java.io.Serializable;

/**
*
* @author Alexander
* @Deprecated 
*/
public class InfoFeriadoJsonObjectVO implements Serializable{
    
    private static final long serialVersionUID = 4457412295698779620L;
    
    private String fecha;
    private boolean feriado = false;
    private String tipoferiado;
    private String descripcion;
    private String respaldo_legal;

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public boolean isFeriado() {
        return feriado;
    }

    public void setFeriado(boolean feriado) {
        this.feriado = feriado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipoferiado() {
        return tipoferiado;
    }

    public void setTipoferiado(String tipoferiado) {
        this.tipoferiado = tipoferiado;
    }

    public String getRespaldo_legal() {
        return respaldo_legal;
    }

    public void setRespaldo_legal(String respaldo_legal) {
        this.respaldo_legal = respaldo_legal;
    }

    @Override
    public String toString() {
        return "InfoFeriadoJsonObjectVO{" + "fecha=" + fecha + ", feriado=" + feriado + ", tipoferiado=" + tipoferiado + ", descripcion=" + descripcion + ", respaldo_legal=" + respaldo_legal + '}';
    }

    
    
}
