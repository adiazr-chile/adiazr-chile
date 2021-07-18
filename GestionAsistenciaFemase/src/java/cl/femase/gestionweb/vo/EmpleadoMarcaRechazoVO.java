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
public class EmpleadoMarcaRechazoVO {
    private String rut;
    private String nombres;
    private String paterno;
    private String materno;
    private String depto_nombre;
    private String cenco_nombre;
    private String email_notificacion;
    private String fecha_hora;
    private String cod_tipo_marca; 
    private String label_tipo_marca; 
    private String fecha_hora_actualizacion;
    private String id_rechazo; 
    private String hashcode; 
    private String motivo_rechazo;
    
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

    /**
     *
     * @param cenco_nombre
     */
    public void setCenco_nombre(String cenco_nombre) {
        this.cenco_nombre = cenco_nombre;
    }

    public String getEmail_notificacion() {
        return email_notificacion;
    }

    public void setEmail_notificacion(String email_notificacion) {
        this.email_notificacion = email_notificacion;
    }

    /**
     *
     * @return
     */
    public String getFecha_hora() {
        return fecha_hora;
    }

    /**
     *
     * @param fecha_hora
     */
    public void setFecha_hora(String fecha_hora) {
        this.fecha_hora = fecha_hora;
    }

    public String getCod_tipo_marca() {
        return cod_tipo_marca;
    }

    /**
     *
     * @param cod_tipo_marca
     */
    public void setCod_tipo_marca(String cod_tipo_marca) {
        this.cod_tipo_marca = cod_tipo_marca;
    }

    public String getLabel_tipo_marca() {
        return label_tipo_marca;
    }

    public void setLabel_tipo_marca(String label_tipo_marca) {
        this.label_tipo_marca = label_tipo_marca;
    }

    public String getFecha_hora_actualizacion() {
        return fecha_hora_actualizacion;
    }

    public void setFecha_hora_actualizacion(String fecha_hora_actualizacion) {
        this.fecha_hora_actualizacion = fecha_hora_actualizacion;
    }

    public String getId_rechazo() {
        return id_rechazo;
    }

    public void setId_rechazo(String id_rechazo) {
        this.id_rechazo = id_rechazo;
    }

    public String getHashcode() {
        return hashcode;
    }

    public void setHashcode(String hashcode) {
        this.hashcode = hashcode;
    }

    public String getMotivo_rechazo() {
        return motivo_rechazo;
    }

    public void setMotivo_rechazo(String motivo_rechazo) {
        this.motivo_rechazo = motivo_rechazo;
    }

    
}
