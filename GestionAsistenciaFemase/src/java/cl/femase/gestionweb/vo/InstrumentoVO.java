
package cl.femase.gestionweb.vo;

import java.io.Serializable;

/**
 *
 * @author adiazr.bolchile.cl
 */
public class InstrumentoVO implements Serializable{
    private static final long serialVersionUID = 426472295622776147L;

    private String nombre;
    private String formatoPrecio;

    public String getFormatoPrecio() {
        return formatoPrecio;
    }

    public void setFormatoPrecio(String formatoPrecio) {
        this.formatoPrecio = formatoPrecio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    
}
