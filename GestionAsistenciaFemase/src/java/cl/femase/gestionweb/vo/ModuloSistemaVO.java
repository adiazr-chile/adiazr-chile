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
public class ModuloSistemaVO implements Serializable{

    private static final long serialVersionUID = 7526472295698776220L;

    private int modulo_id;
    private String modulo_nombre;
    private int estado_id;
    private int orden_despliegue;
    private String iconId;
    
    private String accesoRapido;
    private String titulo;
    private String subTitulo;
    private String imagen;

    public String getAccesoRapido() {
        return accesoRapido;
    }

    public void setAccesoRapido(String accesoRapido) {
        this.accesoRapido = accesoRapido;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSubTitulo() {
        return subTitulo;
    }

    public void setSubTitulo(String subTitulo) {
        this.subTitulo = subTitulo;
    }
    
    
    
    /**
     *
     * @return
     */
    public String getIconId() {
        return iconId;
    }

    public void setIconId(String iconId) {
        this.iconId = iconId;
    }

    public int getModulo_id() {
        return modulo_id;
    }

    public void setModulo_id(int modulo_id) {
        this.modulo_id = modulo_id;
    }

    public String getModulo_nombre() {
        return modulo_nombre;
    }

    public void setModulo_nombre(String modulo_nombre) {
        this.modulo_nombre = modulo_nombre;
    }

    public int getEstado_id() {
        return estado_id;
    }

    public void setEstado_id(int estado_id) {
        this.estado_id = estado_id;
    }

    public int getOrden_despliegue() {
        return orden_despliegue;
    }

    public void setOrden_despliegue(int orden_despliegue) {
        this.orden_despliegue = orden_despliegue;
    }

    
}
