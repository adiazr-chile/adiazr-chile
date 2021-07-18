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
public class AsistenciaTotalesVO implements Serializable{

    private static final long serialVersionUID = 7637369295698779620L;

    private String cencoNombre;
    private String rutEmpleado;
    private String nombreEmpleado;
    private String hrsPresenciales = "00:00";
    private String hrsAtraso        = "00:00";
    private String hrsJustificadas  = "00:00";
    private String hrsExtras        = "00:00";
    private String hrsNoTrabajadas  = "00:00";
    private String hrsTotalDelDia   = "00:00";

    public String getRutEmpleado() {
        return rutEmpleado;
    }

    /**
     *
     * @param rutEmpleado
     */
    public void setRutEmpleado(String rutEmpleado) {
        this.rutEmpleado = rutEmpleado;
    }
    
    public String getCencoNombre() {
        return cencoNombre;
    }

    public void setCencoNombre(String cencoNombre) {
        this.cencoNombre = cencoNombre;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    /**
     *
     * @return
     */
    public String getHrsPresenciales() {
        return hrsPresenciales;
    }

    public void setHrsPresenciales(String hrsPresenciales) {
        this.hrsPresenciales = hrsPresenciales;
    }

    public String getHrsAtraso() {
        return hrsAtraso;
    }

    public void setHrsAtraso(String hrsAtraso) {
        this.hrsAtraso = hrsAtraso;
    }

    public String getHrsJustificadas() {
        return hrsJustificadas;
    }

    public void setHrsJustificadas(String hrsJustificadas) {
        this.hrsJustificadas = hrsJustificadas;
    }

    public String getHrsExtras() {
        return hrsExtras;
    }

    public void setHrsExtras(String hrsExtras) {
        this.hrsExtras = hrsExtras;
    }

    public String getHrsNoTrabajadas() {
        return hrsNoTrabajadas;
    }

    /**
     *
     * @param hrsNoTrabajadas
     */
    public void setHrsNoTrabajadas(String hrsNoTrabajadas) {
        this.hrsNoTrabajadas = hrsNoTrabajadas;
    }

    public String getHrsTotalDelDia() {
        return hrsTotalDelDia;
    }

    public void setHrsTotalDelDia(String hrsTotalDelDia) {
        this.hrsTotalDelDia = hrsTotalDelDia;
    }

}
