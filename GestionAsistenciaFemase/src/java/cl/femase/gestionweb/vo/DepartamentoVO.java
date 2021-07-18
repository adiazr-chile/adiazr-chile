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
public class DepartamentoVO implements Serializable{

    private static final long serialVersionUID = 7597742295698779620L;

    private String id="-1";
    private String nombre;
    private String uploadMessageError;
    private String empresaId;
    private int estado=1;
    
    public DepartamentoVO(String _id, String _nombre, String _empresaId) {
        this.id = _id;
        this.nombre = _nombre;
        this.empresaId = _empresaId; 
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }
    
    
    public DepartamentoVO() {
        
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
   
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     *
     * @return
     */
    public String getUploadMessageError() {
        return uploadMessageError;
    }

    public void setUploadMessageError(String uploadMessageError) {
        this.uploadMessageError = uploadMessageError;
    }

    @Override
    public String toString() {
        return "DepartamentoVO{" + "id=" + id + ", nombre=" + nombre + ", uploadMessageError=" + uploadMessageError + ", empresaId=" + empresaId + ", estado=" + estado + '}';
    }

    
}
