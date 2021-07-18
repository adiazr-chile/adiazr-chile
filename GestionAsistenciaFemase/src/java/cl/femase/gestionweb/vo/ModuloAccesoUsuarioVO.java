
package cl.femase.gestionweb.vo;

import java.io.Serializable;

/**
 *
 * @author adiazr.bolchile.cl
 */
public class ModuloAccesoUsuarioVO implements Serializable{
    private static final long serialVersionUID = 426998295622776147L;

    /*
    SELECT 
  usuario.usr_username, 
  usuario.usr_password, 
  perfil_usuario.perfil_id, 
  modulo_acceso_usuario.modulo_id, 
  modulo_sistema.modulo_nombre, 
  modulo_sistema.orden_despliegue, 
  modulo_acceso_usuario.acceso_id, 
  acceso.acceso_nombre, 
  acceso.acceso_label, 
  modulo_acceso_usuario.tp_acceso_id, 
  modulo_acceso_usuario.orden_despliegue

    */

    private String username;
    private int perfilId;
    private int moduloId;
    private String moduloNombre;
    private int accesoId;
    private String accesoNombre;
    private String accesoLabel;
    private String accesoUrl;
    private int tipoAcceso;

    public ModuloAccesoUsuarioVO(String username, 
            int perfilId, int moduloId, 
            String moduloNombre, int accesoId, 
            String accesoNombre, 
            String accesoLabel,
            String accesoUrl,
            int tipoAcceso) {
        this.username = username;
        this.perfilId = perfilId;
        this.moduloId = moduloId;
        this.moduloNombre = moduloNombre;
        this.accesoId = accesoId;
        this.accesoNombre = accesoNombre;
        this.accesoLabel = accesoLabel;
        this.tipoAcceso = tipoAcceso;
        this.accesoUrl = accesoUrl;
    }

    public String getAccesoUrl() {
        return accesoUrl;
    }

    public void setAccesoUrl(String accesoUrl) {
        this.accesoUrl = accesoUrl;
    }

    
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPerfilId() {
        return perfilId;
    }

    public void setPerfilId(int perfilId) {
        this.perfilId = perfilId;
    }

    public int getModuloId() {
        return moduloId;
    }

    public void setModuloId(int moduloId) {
        this.moduloId = moduloId;
    }

    public String getModuloNombre() {
        return moduloNombre;
    }

    public void setModuloNombre(String moduloNombre) {
        this.moduloNombre = moduloNombre;
    }

    public int getAccesoId() {
        return accesoId;
    }

    public void setAccesoId(int accesoId) {
        this.accesoId = accesoId;
    }

    public String getAccesoNombre() {
        return accesoNombre;
    }

    public void setAccesoNombre(String accesoNombre) {
        this.accesoNombre = accesoNombre;
    }

    public String getAccesoLabel() {
        return accesoLabel;
    }

    public void setAccesoLabel(String accesoLabel) {
        this.accesoLabel = accesoLabel;
    }

    /**
     *
     * @return
     */
    public int getTipoAcceso() {
        return tipoAcceso;
    }

    public void setTipoAcceso(int tipoAcceso) {
        this.tipoAcceso = tipoAcceso;
    }
    
    
}
