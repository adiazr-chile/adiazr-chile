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
public class CalendarioFeriadoVO implements Serializable{

    private static final long serialVersionUID = 7637412295876779620L;

    /**
    * Llave de cada registro: fecha, idTipoFeriado, regionId, comunaId
    */
    private String rowKey;
    
    /**
    * PK
    */
    private String fecha;
    private int dia;
    private int mes;
    private int anio;
    private String label;
    private String observacion;
    private String fechaHoraIngreso;
    private String fechaHoraActualizacion;
    private String irrenunciable;
    private String tipo;
    private String respaldoLegal;
    
    /**
    * PK
    */
    private int idTipoFeriado;
    
    /**
    * PK
    */
    private int regionId;
    
    /**
    * PK
    */
    private int comunaId;
    
    
    public CalendarioFeriadoVO() {
    }

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public String getIrrenunciable() {
        return irrenunciable;
    }

    public void setIrrenunciable(String irrenunciable) {
        this.irrenunciable = irrenunciable;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getRespaldoLegal() {
        return respaldoLegal;
    }

    public void setRespaldoLegal(String respaldoLegal) {
        this.respaldoLegal = respaldoLegal;
    }

    public int getIdTipoFeriado() {
        return idTipoFeriado;
    }

    public void setIdTipoFeriado(int idTipoFeriado) {
        this.idTipoFeriado = idTipoFeriado;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public int getComunaId() {
        return comunaId;
    }

    public void setComunaId(int comunaId) {
        this.comunaId = comunaId;
    }

    public String getFechaHoraIngreso() {
        return fechaHoraIngreso;
    }

    public void setFechaHoraIngreso(String fechaHoraIngreso) {
        this.fechaHoraIngreso = fechaHoraIngreso;
    }

    public String getFechaHoraActualizacion() {
        return fechaHoraActualizacion;
    }

    public void setFechaHoraActualizacion(String fechaHoraActualizacion) {
        this.fechaHoraActualizacion = fechaHoraActualizacion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    
    
}
