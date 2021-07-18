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
public class TurnoVO implements Serializable{

    private static final long serialVersionUID = 6637742295698779620L;

    private int id;
    private String nombre;
    private String empresaId;
    private String cencoNombre;
    private int estado;
    private Date fechaCreacion;
    private String fechaCreacionAsStr;
    private Date fechaModificacion;
    private String fechaModificacionAsStr;
    private String fechaAsignacionStr;
    private String rotativo="N";
    
    public TurnoVO() {
        
    }

    public boolean isRotativo(){
        boolean toreturn = false;
        
        if (this.rotativo.compareTo("S") == 0) toreturn = true;
        
        return toreturn;
    }
    
    public String getRotativo() {
        return rotativo;
    }

    public void setRotativo(String rotativo) {
        this.rotativo = rotativo;
    }

    public String getCencoNombre() {
        return cencoNombre;
    }

    public void setCencoNombre(String cencoNombre) {
        this.cencoNombre = cencoNombre;
    }

    public String getFechaAsignacionStr() {
        return fechaAsignacionStr;
    }

    public void setFechaAsignacionStr(String fechaAsignacionStr) {
        this.fechaAsignacionStr = fechaAsignacionStr;
    }

    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    
    public Date getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(Date fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public String getFechaModificacionAsStr() {
        return fechaModificacionAsStr;
    }

    public void setFechaModificacionAsStr(String fechaModificacionAsStr) {
        this.fechaModificacionAsStr = fechaModificacionAsStr;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }

    /**
     *
     * @param nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getFechaCreacionAsStr() {
        return fechaCreacionAsStr;
    }

    /**
     *
     * @param fechaCreacionAsStr
     */
    public void setFechaCreacionAsStr(String fechaCreacionAsStr) {
        this.fechaCreacionAsStr = fechaCreacionAsStr;
    }

    
}
