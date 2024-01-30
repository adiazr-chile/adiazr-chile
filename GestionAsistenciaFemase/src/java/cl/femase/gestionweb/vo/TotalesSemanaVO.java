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
public class TotalesSemanaVO implements Serializable{

    private static final long serialVersionUID = 7594582295698776220L;

    private String totalHrsPresenciales = "00:00";
    private String totalHrsTrabajadas = "00:00";
    private String totalHrsNoTrabajadas = "00:00";
    private String totalHrsTeoricas = "00:00";
    private String totalHrsAusencia = "00:00";
    private String totalHrsExtras = "00:00";
    private String totalHrsAtraso = "00:00";
    private String totalHrsJustificadas = "00:00";
    private int diasTrabajados  = 0;
    private int diasAusente     = 0;

    public String getTotalHrsNoTrabajadas() {
        return totalHrsNoTrabajadas;
    }

    public void setTotalHrsNoTrabajadas(String totalHrsNoTrabajadas) {
        this.totalHrsNoTrabajadas = totalHrsNoTrabajadas;
    }

    public String getTotalHrsTeoricas() {
        return totalHrsTeoricas;
    }

    public void setTotalHrsTeoricas(String totalHrsTeoricas) {
        this.totalHrsTeoricas = totalHrsTeoricas;
    }

    public int getDiasTrabajados() {
        return diasTrabajados;
    }

    public void setDiasTrabajados(int diasTrabajados) {
        this.diasTrabajados = diasTrabajados;
    }

    public int getDiasAusente() {
        return diasAusente;
    }

    public void setDiasAusente(int diasAusente) {
        this.diasAusente = diasAusente;
    }

    public String getTotalHrsPresenciales() {
        return totalHrsPresenciales;
    }

    public void setTotalHrsPresenciales(String totalHrsPresenciales) {
        this.totalHrsPresenciales = totalHrsPresenciales;
    }

    public String getTotalHrsTrabajadas() {
        return totalHrsTrabajadas;
    }

    public void setTotalHrsTrabajadas(String totalHrsTrabajadas) {
        this.totalHrsTrabajadas = totalHrsTrabajadas;
    }

    public String getTotalHrsAusencia() {
        return totalHrsAusencia;
    }

    public void setTotalHrsAusencia(String totalHrsAusencia) {
        this.totalHrsAusencia = totalHrsAusencia;
    }

    public String getTotalHrsExtras() {
        return totalHrsExtras;
    }

    public void setTotalHrsExtras(String totalHrsExtras) {
        this.totalHrsExtras = totalHrsExtras;
    }

    public String getTotalHrsAtraso() {
        return totalHrsAtraso;
    }

    public void setTotalHrsAtraso(String totalHrsAtraso) {
        this.totalHrsAtraso = totalHrsAtraso;
    }

    public String getTotalHrsJustificadas() {
        return totalHrsJustificadas;
    }

    public void setTotalHrsJustificadas(String totalHrsJustificadas) {
        this.totalHrsJustificadas = totalHrsJustificadas;
    }

    @Override
    public String toString() {
        return "TotalesSemanaVO{" + "totalHrsPresenciales=" + totalHrsPresenciales + ", totalHrsTrabajadas=" + totalHrsTrabajadas + ", totalHrsNoTrabajadas=" + totalHrsNoTrabajadas + ", totalHrsTeoricas=" + totalHrsTeoricas + ", totalHrsAusencia=" + totalHrsAusencia + ", totalHrsExtras=" + totalHrsExtras + ", totalHrsAtraso=" + totalHrsAtraso + ", totalHrsJustificadas=" + totalHrsJustificadas + ", diasTrabajados=" + diasTrabajados + ", diasAusente=" + diasAusente + '}';
    }

    
}
