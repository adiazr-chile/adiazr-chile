/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.vo;

import java.io.Serializable;
import java.util.StringTokenizer;

/**
 *
 * @author Alexander
 */
public class MarcasEventosVO implements Serializable{
    private static final long serialVersionUID = 6549472338799776220L;
   
    private int correlativo;
    private String codDispositivo;
    private String empresaCod;
    private String rutEmpleado;
    private String id;
    private String hashcode;
    private String fechaHoraModificacion;
    private String codUsuario;

    private String fechaHoraOriginal;
    private int tipoMarcaOriginal;
    private String comentarioOriginal;
    
    private String fechaHoraNew;
    private int tipoMarcaNew;
    private String comentarioNew;
   
    /**
    * 'M': Modificada, 'E': Eliminada 
    */
    private String tipoEvento;

    /**
    * 'M': Modificada, 'E': Eliminada 
    */
    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public int getCorrelativo() {
        return correlativo;
    }

    public void setCorrelativo(int correlativo) {
        this.correlativo = correlativo;
    }

    public String getCodDispositivo() {
        return codDispositivo;
    }

    public void setCodDispositivo(String codDispositivo) {
        this.codDispositivo = codDispositivo;
    }

    /**
     *
     * @return
     */
    public String getEmpresaCod() {
        return empresaCod;
    }

    public void setEmpresaCod(String empresaCod) {
        this.empresaCod = empresaCod;
    }

    public String getRutEmpleado() {
        return rutEmpleado;
    }

    public void setRutEmpleado(String rutEmpleado) {
        this.rutEmpleado = rutEmpleado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHashcode() {
        return hashcode;
    }

    public void setHashcode(String hashcode) {
        this.hashcode = hashcode;
    }

    public String getFechaHoraModificacion() {
        return fechaHoraModificacion;
    }

    public void setFechaHoraModificacion(String fechaHoraModificacion) {
        this.fechaHoraModificacion = fechaHoraModificacion;
    }

    public String getCodUsuario() {
        return codUsuario;
    }

    /**
     *
     * @param codUsuario
     */
    public void setCodUsuario(String codUsuario) {
        this.codUsuario = codUsuario;
    }

    public String getFechaHoraOriginal() {
        return fechaHoraOriginal;
    }

    public void setFechaHoraOriginal(String fechaHoraOriginal) {
        this.fechaHoraOriginal = fechaHoraOriginal;
    }

    public int getTipoMarcaOriginal() {
        return tipoMarcaOriginal;
    }

    public void setTipoMarcaOriginal(int tipoMarcaOriginal) {
        this.tipoMarcaOriginal = tipoMarcaOriginal;
    }

    public String getComentarioOriginal() {
        return comentarioOriginal;
    }

    public void setComentarioOriginal(String comentarioOriginal) {
        this.comentarioOriginal = comentarioOriginal;
    }

    /**
     *
     * @return
     */
    public String getFechaHoraNew() {
        return fechaHoraNew;
    }

    public void setFechaHoraNew(String fechaHoraNew) {
        this.fechaHoraNew = fechaHoraNew;
    }

    /**
     *
     * @return
     */
    public int getTipoMarcaNew() {
        return tipoMarcaNew;
    }

    public void setTipoMarcaNew(int tipoMarcaNew) {
        this.tipoMarcaNew = tipoMarcaNew;
    }

    public String getComentarioNew() {
        return comentarioNew;
    }

    public void setComentarioNew(String comentarioNew) {
        this.comentarioNew = comentarioNew;
    }
    
    
    
    
}
