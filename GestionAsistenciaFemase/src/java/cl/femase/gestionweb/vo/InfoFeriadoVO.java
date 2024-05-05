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
public class InfoFeriadoVO implements Serializable{
    
    private static final long serialVersionUID = 4457412295698779620L;
    
    private String fecha;
    private boolean feriado = false;
    private String tipoFeriado;
    private String descripcion;
    private String respaldoLegal;

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

    public String getTipoFeriado() {
        return tipoFeriado;
    }

    public void setTipoFeriado(String tipoFeriado) {
        this.tipoFeriado = tipoFeriado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRespaldoLegal() {
        return respaldoLegal;
    }

    public void setRespaldoLegal(String respaldoLegal) {
        this.respaldoLegal = respaldoLegal;
    }
    
}
