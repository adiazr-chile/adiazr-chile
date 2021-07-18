
package cl.femase.gestionweb.vo;

import java.io.Serializable;

/**
 *
 * @author adiazr.bolchile.cl
 */
public class DestinatarioSolicitudVO implements Serializable{
    private static final long serialVersionUID = 984472295622776147L;

    private final String nombre;
    private final String email;

    public DestinatarioSolicitudVO(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }
    
    public String getEmail() {
        return email;
    }

}
