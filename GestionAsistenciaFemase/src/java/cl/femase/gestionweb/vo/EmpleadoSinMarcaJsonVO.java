/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.vo;

/**
 *
 * @author Alexander
 */
public class EmpleadoSinMarcaJsonVO {
    private String rut;
    private String nombres;
    private String paterno;
    private String materno;
    private String depto_nombre;
    private String cenco_nombre;
    private String email_notificacion;
    
    public String getRut() {
        return rut;
    }

    /**
     *
     * @param rut
     */
    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getPaterno() {
        return paterno;
    }

    /**
     *
     * @param paterno
     */
    public void setPaterno(String paterno) {
        this.paterno = paterno;
    }

    public String getMaterno() {
        return materno;
    }

    public void setMaterno(String materno) {
        this.materno = materno;
    }

    public String getDepto_nombre() {
        return depto_nombre;
    }

    public void setDepto_nombre(String depto_nombre) {
        this.depto_nombre = depto_nombre;
    }

    public String getCenco_nombre() {
        return cenco_nombre;
    }

    public void setCenco_nombre(String cenco_nombre) {
        this.cenco_nombre = cenco_nombre;
    }

    public String getEmail_notificacion() {
        return email_notificacion;
    }

    public void setEmail_notificacion(String email_notificacion) {
        this.email_notificacion = email_notificacion;
    }

}
