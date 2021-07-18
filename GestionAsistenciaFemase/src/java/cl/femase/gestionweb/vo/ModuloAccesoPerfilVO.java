
package cl.femase.gestionweb.vo;

import java.io.Serializable;

/**
 *
 * @author adiazr.bolchile.cl
 */
public class ModuloAccesoPerfilVO implements Serializable{
    private static final long serialVersionUID = 426998295622776147L;

    private String username;
    private int perfilId;
    private String perfilNombre;
    private int moduloId;
    private String moduloNombre;
    private int accesoId;
    private String accesoNombre;
    private String accesoLabel;
    private String accesoUrl;
    private int tipoAcceso;
    private int ordenDespliegue;
    
    private String accesoRapido;
    private String moduloTitle;
    private String moduloSubTitle;
    private String moduloImagen;

    public ModuloAccesoPerfilVO() {
    }

    /**
     *
     * @param username
     * @param perfilId
     * @param moduloId
     * @param moduloNombre
     * @param accesoId
     * @param accesoNombre
     * @param accesoLabel
     * @param accesoUrl
     * @param tipoAcceso
     */
    public ModuloAccesoPerfilVO(String username, 
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

    public String getAccesoRapido() {
        return accesoRapido;
    }

    public void setAccesoRapido(String accesoRapido) {
        this.accesoRapido = accesoRapido;
    }

    public String getModuloImagen() {
        return moduloImagen;
    }

    public void setModuloImagen(String moduloImagen) {
        this.moduloImagen = moduloImagen;
    }

    public String getModuloTitle() {
        return moduloTitle;
    }

    public void setModuloTitle(String moduloTitle) {
        this.moduloTitle = moduloTitle;
    }

    public String getModuloSubTitle() {
        return moduloSubTitle;
    }

    public void setModuloSubTitle(String moduloSubTitle) {
        this.moduloSubTitle = moduloSubTitle;
    }

    public String getPerfilNombre() {
        return perfilNombre;
    }

    public void setPerfilNombre(String perfilNombre) {
        this.perfilNombre = perfilNombre;
    }

    
    public int getOrdenDespliegue() {
        return ordenDespliegue;
    }

    /**
     *
     * @param ordenDespliegue
     */
    public void setOrdenDespliegue(int ordenDespliegue) {
        this.ordenDespliegue = ordenDespliegue;
    }

    /**
     *
     * @return
     */
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

    /**
     *
     * @return
     */
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

    /**
     *
     * @param accesoNombre
     */
    public void setAccesoNombre(String accesoNombre) {
        this.accesoNombre = accesoNombre;
    }

    public String getAccesoLabel() {
        return accesoLabel;
    }

    public void setAccesoLabel(String accesoLabel) {
        this.accesoLabel = accesoLabel;
    }

    public int getTipoAcceso() {
        return tipoAcceso;
    }

    public void setTipoAcceso(int tipoAcceso) {
        this.tipoAcceso = tipoAcceso;
    }
    
    
}
