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
public class PerfilUsuarioVO implements Serializable{

    private static final long serialVersionUID = 7009472295698776220L;

    private int id;
    private String nombre;
    private int estadoId;
    private String estadoNombre;
    private String adminEmpresa;

    public int getId() {
        return id;
    }

    /**
     *
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(int estadoId) {
        this.estadoId = estadoId;
    }

    public String getEstadoNombre() {
        return estadoNombre;
    }

    public void setEstadoNombre(String estadoNombre) {
        this.estadoNombre = estadoNombre;
    }

    public String getAdminEmpresa() {
        return adminEmpresa;
    }

    public void setAdminEmpresa(String adminEmpresa) {
        this.adminEmpresa = adminEmpresa;
    }
   
}
