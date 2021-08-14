
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
    private final String cargo;

    public DestinatarioSolicitudVO(String _nombre, 
            String _email, String _cargo) {
        this.nombre = _nombre;
        this.email  = _email;
        this.cargo  = _cargo;
    }

    public String getNombre() {
        return nombre;
    }
    
    public String getEmail() {
        return email;
    }

    public String getCargo() {
        return cargo;
    }
    
}
