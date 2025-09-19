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
public class UsuariosByPerfilVO implements Serializable{

    private static final long serialVersionUID = 7637412445698779620L;

    private final String nombrePerfil;
    private final int countUsuarios;

    public UsuariosByPerfilVO(String nombrePerfil, int countUsuarios) {
        this.nombrePerfil = nombrePerfil;
        this.countUsuarios = countUsuarios;
    }

    public String getNombrePerfil() {
        return nombrePerfil;
    }

    public int getCountUsuarios() {
        return countUsuarios;
    }
        
    
    
    
}
